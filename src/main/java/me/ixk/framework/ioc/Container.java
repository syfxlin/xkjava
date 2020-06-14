package me.ixk.framework.ioc;

import cn.hutool.core.convert.Convert;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.ixk.framework.annotations.PreDestroy;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.DynamicInterceptor;
import me.ixk.framework.exceptions.ContainerException;
import me.ixk.framework.factory.AfterInitProcessor;
import me.ixk.framework.ioc.injector.DefaultMethodInjector;
import me.ixk.framework.ioc.injector.DefaultParameterInjector;
import me.ixk.framework.ioc.injector.DefaultPropertyInjector;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.AutowireUtils;
import me.ixk.framework.utils.ClassUtils;
import net.sf.cglib.proxy.Enhancer;

public class Container {
    protected final ParameterInjector parameterInjector = new DefaultParameterInjector();
    protected final PropertyInjector propertyInjector = new DefaultPropertyInjector();
    protected final MethodInjector methodInjector = new DefaultMethodInjector();

    protected final Map<String, Binding> bindings;

    protected final Map<String, Object> instances;

    protected final Map<String, String> aliases;

    protected Map<String, Object> globalArgs = new ConcurrentHashMap<>();

    protected Map<String, Object> resetGlobalArgs;

    protected List<AfterInitProcessor> afterInitProcessors = new ArrayList<>();

    public Container() {
        this.bindings = new ConcurrentHashMap<>();
        this.instances = new ConcurrentHashMap<>();
        this.aliases = new ConcurrentHashMap<>();
    }

    /* base */

    public Map<String, Object> getGlobalArgs() {
        return globalArgs;
    }

    public void setGlobalArgs(Map<String, Object> globalArgs) {
        this.resetGlobalArgs = this.globalArgs;
        this.globalArgs = globalArgs;
    }

    public void resetGlobalArgs() {
        this.globalArgs = this.resetGlobalArgs;
    }

    public void clearGlobalArgs() {
        this.globalArgs = new ConcurrentHashMap<>();
    }

    public List<AfterInitProcessor> getAfterInitProcessors() {
        return afterInitProcessors;
    }

    public void setAfterInitProcessors(
        List<AfterInitProcessor> afterInitProcessors
    ) {
        this.afterInitProcessors = afterInitProcessors;
    }

    public void addObjectAfterInitProcessor(
        AfterInitProcessor afterInitProcessor
    ) {
        this.afterInitProcessors.add(afterInitProcessor);
    }

    public Container alias(String alias, String _abstract) {
        if (_abstract.equals(alias)) {
            return this;
        }
        this.aliases.put(alias, _abstract);
        return this;
    }

    public boolean has(String _abstract) {
        return this.hasBinding(_abstract);
    }

    public boolean has(Class<?> _abstract) {
        return this.hasBinding(_abstract.getName());
    }

    protected void setBinding(
        String _abstract,
        String concrete,
        boolean shared,
        boolean overwrite
    ) {
        _abstract = this.getAbstractByAlias(_abstract);
        Concrete concrete1 = (container, args) ->
            container.build(concrete, args);
        this.setBinding(_abstract, concrete1, shared, overwrite);
    }

    protected void setBinding(
        String _abstract,
        Concrete concrete,
        boolean shared,
        boolean overwrite
    ) {
        if (!overwrite && shared && this.bindings.containsKey(_abstract)) {
            throw new RuntimeException(
                "Target [" + _abstract + "] is a singleton and has been bind"
            );
        }
        this.bindings.put(_abstract, new Binding(concrete, shared));
    }

    protected Binding getBinding(String _abstract) {
        _abstract = this.getAbstractByAlias(_abstract);
        String finalAbstract = _abstract;
        // 自动创建binding
        return this.bindings.getOrDefault(
                _abstract,
                new Binding(
                    (container, args) -> container.build(finalAbstract, args),
                    false
                )
            );
    }

    protected boolean hasBinding(String _abstract) {
        _abstract = this.getAbstractByAlias(_abstract);
        return this.bindings.containsKey(_abstract);
    }

    protected String[] getAbstractAndAliasByAlias(
        String alias,
        String inAlias
    ) {
        String _abstract = this.getAbstractByAlias(alias);
        if (alias.equals(_abstract)) {
            return new String[] { _abstract, inAlias };
        }
        if (inAlias == null) {
            return new String[] { _abstract, alias };
        }
        return new String[] { _abstract, inAlias };
    }

    protected String getAbstractByAlias(String alias) {
        return this.aliases.getOrDefault(alias, alias);
    }

    /* doing */

    protected Container doBind(
        String _abstract,
        Concrete concrete,
        boolean shared,
        String alias,
        boolean overwrite
    ) {
        this.setBinding(_abstract, concrete, shared, overwrite);
        if (alias != null) {
            this.alias(alias, _abstract);
        }
        return this;
    }

    protected Container doInstance(
        String _abstract,
        Object instance,
        String alias
    ) {
        String[] abstractAlias =
            this.getAbstractAndAliasByAlias(_abstract, alias);
        _abstract = abstractAlias[0];
        alias = abstractAlias[1];
        // 处理 Set 注入和方法
        instance = this.methodInjector.inject(this, instance, this.globalArgs);
        // 解决动态注入
        try {
            instance =
                AutowireUtils.resolveAutowiringValue(
                    instance,
                    Class.forName(_abstract)
                );
        } catch (ClassNotFoundException e) {
            // no code
        }
        Object finalInstance = instance;
        this.instances.put(_abstract, finalInstance);
        this.bind(
                _abstract,
                (container, args) -> finalInstance,
                true,
                alias,
                true
            );
        return this;
    }

    protected Object doBuild(Class<?> _class, Map<String, Object> args) {
        Map<String, List<Advice>> map = null;
        if (this.instances.containsKey(AspectManager.class.getName())) {
            try {
                map =
                    (
                        (AspectManager) this.instances.get(
                                AspectManager.class.getName()
                            )
                    ).matches(_class);
            } catch (Throwable e) {
                throw new ContainerException(
                    "Instance build failed (Aspect matches)",
                    e
                );
            }
        }
        Constructor<?>[] constructors = _class.getDeclaredConstructors();
        Object instance;
        if (constructors.length == 1) {
            Constructor<?> constructor = constructors[0];
            Object[] dependencies =
                this.parameterInjector.inject(this, constructor, args);
            try {
                if (map == null || map.isEmpty()) {
                    instance = constructor.newInstance(dependencies);
                } else {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(_class);
                    //                    enhancer.setInterfaces(constructor.getParameterTypes());
                    enhancer.setCallback(new DynamicInterceptor(map));
                    instance =
                        enhancer.create(
                            constructor.getParameterTypes(),
                            dependencies
                        );
                }
            } catch (Exception e) {
                throw new ContainerException("Instantiated object failed", e);
            }
        } else {
            // 不允许构造器重载
            throw new RuntimeException(
                "The bound instance must have only one constructor"
            );
        }
        instance = this.propertyInjector.inject(this, instance, args);
        return this.methodInjector.inject(this, instance, args);
    }

    protected Object doAfterProcessor(Object instance, Class<?> returnType) {
        for (AfterInitProcessor processor : this.getAfterInitProcessors()) {
            instance = processor.process(instance, returnType);
            if (instance == null) {
                return null;
            }
        }
        return instance;
    }

    protected <T> T doMake(
        String _abstract,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        _abstract = this.getAbstractByAlias(_abstract);
        Binding binding = this.getBinding(_abstract);
        Object instance;
        if (binding.isShared() && this.instances.containsKey(_abstract)) {
            instance = this.instances.get(_abstract);
        } else {
            try {
                instance = binding.getConcrete().invoke(this, args);
            } catch (Throwable e) {
                throw new ContainerException("Instance make failed", e);
            }
        }
        // 解决动态注入
        instance = AutowireUtils.resolveAutowiringValue(instance, returnType);
        if (binding.isShared() && !this.instances.containsKey(_abstract)) {
            this.instances.put(_abstract, instance);
        }
        instance = this.doAfterProcessor(instance, returnType);
        return Convert.convert(returnType, instance);
    }

    protected void doRemove(String _abstract) {
        String alias = _abstract;
        _abstract = this.getAbstractByAlias(_abstract);

        Object instance = this.make(_abstract);
        Set<Method> methods = ClassUtils.getMethods(instance);
        for (Method method : methods) {
            // init 方法
            PreDestroy postConstruct = AnnotationUtils.getAnnotation(
                method,
                PreDestroy.class
            );
            if (postConstruct != null) {
                this.call(instance, method, Object.class, this.globalArgs);
            }
        }

        for (Map.Entry<String, String> entry : this.aliases.entrySet()) {
            if (entry.getKey().equals(alias)) {
                this.aliases.remove(entry.getKey());
                break;
            } else if (entry.getValue().equals(_abstract)) {
                this.aliases.remove(entry.getValue());
                break;
            }
        }
        this.bindings.remove(_abstract);
        this.instances.remove(_abstract);
    }

    protected <T> T callMethod(
        Object object,
        Method method,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        Object[] dependencies =
            this.parameterInjector.inject(this, method, args);
        try {
            return Convert.convert(
                returnType,
                method.invoke(object, dependencies)
            );
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ContainerException("Method call failed", e);
        }
    }

    protected <T> T callObjectMethod(
        Object instance,
        String methodName,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        Method[] methods = Arrays
            .stream(instance.getClass().getMethods())
            .filter(m -> m.getName().equals(methodName))
            .toArray(Method[]::new);
        if (methods.length == 0) {
            throw new RuntimeException("The specified method was not found");
        } else if (methods.length > 1) {
            throw new RuntimeException(
                "The called method cannot be overloaded"
            );
        }
        return this.callMethod(instance, methods[0], returnType, args);
    }

    protected <T> T callArrayMethod(
        String[] target,
        Class<T> returnType,
        Map<String, Object> args,
        Map<String, Object> newArgs
    ) {
        Object object = this.make(target[0], Object.class, newArgs);
        return this.callObjectMethod(object, target[1], returnType, args);
    }

    protected <T> T callArrayFormTypes(
        String[] target,
        Class<?>[] paramTypes,
        Class<T> returnType,
        Map<String, Object> args,
        Map<String, Object> newArgs
    ) {
        Object object = this.make(target[0], Object.class, newArgs);
        Method method;
        try {
            method = object.getClass().getMethod(target[1], paramTypes);
        } catch (NoSuchMethodException e) {
            throw new ContainerException(
                "Corresponding methods not found in the specified class",
                e
            );
        }
        return this.callMethod(object, method, returnType, args);
    }

    /* bind String, String */

    public Container bind(String _abstract) {
        return this.bind(_abstract, _abstract, false, null, false);
    }

    public Container bind(String _abstract, String concrete) {
        return this.bind(_abstract, concrete, false, null, false);
    }

    public Container bind(String _abstract, String concrete, boolean shared) {
        return this.bind(_abstract, concrete, shared, null, false);
    }

    public Container bind(String _abstract, String concrete, String alias) {
        return this.bind(_abstract, concrete, false, alias, false);
    }

    public Container bind(
        String _abstract,
        String concrete,
        boolean shared,
        String alias
    ) {
        return this.bind(_abstract, concrete, shared, alias, false);
    }

    /* bind final String, String */
    public Container bind(
        String _abstract,
        String concrete,
        boolean shared,
        String alias,
        boolean overwrite
    ) {
        String[] abstractAlias =
            this.getAbstractAndAliasByAlias(_abstract, alias);
        _abstract = abstractAlias[0];
        alias = abstractAlias[1];
        if (concrete == null) {
            concrete = _abstract;
        }
        String finalConcrete = concrete;
        return this.bind(
                _abstract,
                (Container container, Map<String, Object> args) ->
                    container.build(finalConcrete, args),
                shared,
                alias,
                overwrite
            );
    }

    /* bind String, Concrete */

    public Container bind(String _abstract, Concrete concrete) {
        return this.bind(_abstract, concrete, false, null, false);
    }

    public Container bind(String _abstract, Concrete concrete, boolean shared) {
        return this.bind(_abstract, concrete, shared, null, false);
    }

    public Container bind(String _abstract, Concrete concrete, String alias) {
        return this.bind(_abstract, concrete, false, alias, false);
    }

    public Container bind(
        String _abstract,
        Concrete concrete,
        boolean shared,
        String alias
    ) {
        return this.bind(_abstract, concrete, shared, alias, false);
    }

    /* bind final String, Concrete */
    public Container bind(
        String _abstract,
        Concrete concrete,
        boolean shared,
        String alias,
        boolean overwrite
    ) {
        return this.doBind(_abstract, concrete, shared, alias, overwrite);
    }

    /* bind Class, Class */

    public Container bind(Class<?> _abstract) {
        return this.bind(_abstract, _abstract, false, null, false);
    }

    public Container bind(Class<?> _abstract, Class<?> concrete) {
        return this.bind(_abstract, concrete, false, null, false);
    }

    public Container bind(
        Class<?> _abstract,
        Class<?> concrete,
        boolean shared
    ) {
        return this.bind(_abstract, concrete, shared, null, false);
    }

    public Container bind(
        Class<?> _abstract,
        Class<?> concrete,
        boolean shared,
        String alias
    ) {
        return this.bind(_abstract, concrete, shared, alias, false);
    }

    /* bind final Class, Class */
    public Container bind(
        Class<?> _abstract,
        Class<?> concrete,
        boolean shared,
        String alias,
        boolean overwrite
    ) {
        return this.bind(
                _abstract.getName(),
                concrete.getName(),
                shared,
                alias,
                overwrite
            );
    }

    /* bind Class, Concrete */

    public Container bind(Class<?> _abstract, Concrete concrete) {
        return this.bind(_abstract, concrete, false, null, false);
    }

    public Container bind(
        Class<?> _abstract,
        Concrete concrete,
        boolean shared
    ) {
        return this.bind(_abstract, concrete, shared, null, false);
    }

    public Container bind(Class<?> _abstract, Concrete concrete, String alias) {
        return this.bind(_abstract, concrete, false, alias, false);
    }

    public Container bind(
        Class<?> _abstract,
        Concrete concrete,
        boolean shared,
        String alias
    ) {
        return this.bind(_abstract, concrete, shared, alias, false);
    }

    /* bind final Class, Concrete */
    public Container bind(
        Class<?> _abstract,
        Concrete concrete,
        boolean shared,
        String alias,
        boolean overwrite
    ) {
        return this.bind(
                _abstract.getName(),
                concrete,
                shared,
                alias,
                overwrite
            );
    }

    /* singleton String, String */

    public Container singleton(String _abstract) {
        return this.singleton(_abstract, _abstract, null, false);
    }

    public Container singleton(String _abstract, String concrete) {
        return this.singleton(_abstract, concrete, null, false);
    }

    public Container singleton(
        String _abstract,
        String concrete,
        String alias
    ) {
        return this.singleton(_abstract, concrete, alias, false);
    }

    /* singleton final String, String */
    public Container singleton(
        String _abstract,
        String concrete,
        String alias,
        boolean overwrite
    ) {
        return this.bind(_abstract, concrete, true, alias, overwrite);
    }

    /* singleton String, Concrete */

    public Container singleton(String _abstract, Concrete concrete) {
        return this.singleton(_abstract, concrete, null, false);
    }

    public Container singleton(
        String _abstract,
        Concrete concrete,
        String alias
    ) {
        return this.singleton(_abstract, concrete, alias, false);
    }

    /* singleton final String, Concrete */
    public Container singleton(
        String _abstract,
        Concrete concrete,
        String alias,
        boolean overwrite
    ) {
        return this.bind(_abstract, concrete, true, alias, overwrite);
    }

    /* singleton Class, Class */

    public Container singleton(Class<?> _abstract) {
        return this.singleton(_abstract, _abstract, null, false);
    }

    public Container singleton(Class<?> _abstract, Class<?> concrete) {
        return this.singleton(_abstract, concrete, null, false);
    }

    public Container singleton(
        Class<?> _abstract,
        Class<?> concrete,
        String alias
    ) {
        return this.singleton(_abstract, concrete, alias, false);
    }

    /* singleton final Class, Class */
    public Container singleton(
        Class<?> _abstract,
        Class<?> concrete,
        String alias,
        boolean overwrite
    ) {
        return this.bind(_abstract, concrete, true, alias, overwrite);
    }

    /* singleton Class, Concrete */

    public Container singleton(Class<?> _abstract, Concrete concrete) {
        return this.singleton(_abstract, concrete, null, false);
    }

    public Container singleton(
        Class<?> _abstract,
        Concrete concrete,
        String alias
    ) {
        return this.singleton(_abstract, concrete, alias, false);
    }

    /* singleton final Class, Concrete */
    public Container singleton(
        Class<?> _abstract,
        Concrete concrete,
        String alias,
        boolean overwrite
    ) {
        return this.bind(_abstract, concrete, true, alias, overwrite);
    }

    /* instance String, Object */

    public Container instance(String _abstract, Object instance) {
        return this.instance(_abstract, instance, null);
    }

    /* instance final String, Object */
    public Container instance(String _abstract, Object instance, String alias) {
        return this.doInstance(_abstract, instance, alias);
    }

    /* instance Class, Object */

    public Container instance(Class<?> _abstract, Object instance) {
        return this.instance(_abstract.getName(), instance, null);
    }

    /* instance final Class, Object */
    public Container instance(
        Class<?> _abstract,
        Object instance,
        String alias
    ) {
        return this.instance(_abstract.getName(), instance, alias);
    }

    /* build Concrete */

    public Object build(Concrete concrete, Map<String, Object> args) {
        try {
            return concrete.invoke(this, args);
        } catch (Throwable e) {
            throw new ContainerException("Instance build failed", e);
        }
    }

    /* build String */

    public Object build(String _class, Map<String, Object> args) {
        try {
            return this.build(Class.forName(_class), args);
        } catch (ClassNotFoundException e) {
            throw new ContainerException("Instance build failed", e);
        }
    }

    /* build Class */

    public Object build(Class<?> _class, Map<String, Object> args) {
        return this.doBuild(_class, args);
    }

    /* make String */

    public Object make(String _abstract) {
        return this.make(_abstract, Object.class);
    }

    public <T> T make(String _abstract, Class<T> returnType) {
        return this.make(_abstract, returnType, this.globalArgs);
    }

    public <T> T make(
        String _abstract,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.doMake(_abstract, returnType, args);
    }

    /* make Class */

    public <T> T make(Class<T> _abstract) {
        return this.make(_abstract.getName(), _abstract, this.globalArgs);
    }

    public <T> T make(Class<T> _abstract, Map<String, Object> args) {
        return this.make(_abstract.getName(), _abstract, args);
    }

    /* call String[] */

    public <T> T call(String[] target, Class<T> returnType) {
        return this.call(target, returnType, this.globalArgs);
    }

    public <T> T call(
        String[] target,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(target, returnType, args, this.globalArgs);
    }

    public <T> T call(
        String[] target,
        Class<T> returnType,
        Map<String, Object> args,
        Map<String, Object> newArgs
    ) {
        return this.callArrayMethod(target, returnType, args, newArgs);
    }

    public <T> T call(
        String[] target,
        Class<?>[] paramTypes,
        Class<T> returnType
    ) {
        return this.call(target, paramTypes, returnType, this.globalArgs);
    }

    public <T> T call(
        String[] target,
        Class<?>[] paramTypes,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(target, paramTypes, returnType, args, this.globalArgs);
    }

    public <T> T call(
        String[] target,
        Class<?>[] paramTypes,
        Class<T> returnType,
        Map<String, Object> args,
        Map<String, Object> newArgs
    ) {
        return this.callArrayFormTypes(
                target,
                paramTypes,
                returnType,
                args,
                newArgs
            );
    }

    /* call String */

    public <T> T call(String target, Class<T> returnType) {
        return this.call(target, returnType, this.globalArgs);
    }

    public <T> T call(
        String target,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(target, returnType, args, this.globalArgs);
    }

    public <T> T call(
        String target,
        Class<T> returnType,
        Map<String, Object> args,
        Map<String, Object> newArgs
    ) {
        return this.call(target.split("@"), returnType, args, newArgs);
    }

    public <T> T call(
        String target,
        Class<?>[] paramTypes,
        Class<T> returnType
    ) {
        return this.call(target, paramTypes, returnType, this.globalArgs);
    }

    public <T> T call(
        String target,
        Class<?>[] paramTypes,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(target, paramTypes, returnType, args, this.globalArgs);
    }

    public <T> T call(
        String target,
        Class<?>[] paramTypes,
        Class<T> returnType,
        Map<String, Object> args,
        Map<String, Object> newArgs
    ) {
        return this.call(
                target.split("@"),
                paramTypes,
                returnType,
                args,
                newArgs
            );
    }

    /* call Class, Method */

    public <T> T call(Class<?> _class, Method method, Class<T> returnType) {
        return this.call(_class, method, returnType, this.globalArgs);
    }

    public <T> T call(
        Class<?> _class,
        Method method,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(_class, method, returnType, args, this.globalArgs);
    }

    public <T> T call(
        Class<?> _class,
        Method method,
        Class<T> returnType,
        Map<String, Object> args,
        Map<String, Object> newArgs
    ) {
        return this.call(this.make(_class, newArgs), method, returnType, args);
    }

    /* call Method */

    public <T> T call(Method method, Class<T> returnType) {
        return this.call(method, returnType, this.globalArgs);
    }

    public <T> T call(
        Method method,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(method, returnType, args, this.globalArgs);
    }

    public <T> T call(
        Method method,
        Class<T> returnType,
        Map<String, Object> args,
        Map<String, Object> newArgs
    ) {
        return this.call(
                method.getDeclaringClass(),
                method,
                returnType,
                args,
                newArgs
            );
    }

    /* call Object, Method */

    public <T> T call(Object instance, Method method, Class<T> returnType) {
        return this.call(instance, method, returnType, this.globalArgs);
    }

    public <T> T call(
        Object instance,
        Method method,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.callMethod(instance, method, returnType, args);
    }

    public <T> T call(Object instance, String methodName, Class<T> returnType) {
        return this.call(instance, methodName, returnType, this.globalArgs);
    }

    public <T> T call(
        Object instance,
        String methodName,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.callObjectMethod(instance, methodName, returnType, args);
    }

    public <T> T call(Class<?> _class, String methodName, Class<T> returnType) {
        return this.call(_class, methodName, returnType, this.globalArgs);
    }

    public <T> T call(
        Class<?> _class,
        String methodName,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.callObjectMethod(
                this.make(_class, args),
                methodName,
                returnType,
                args
            );
    }

    public void remove(String _abstract) {
        this.doRemove(_abstract);
    }

    public void remove(Class<?> _abstract) {
        this.remove(_abstract.getName());
    }

    public Object[] parameterInjector(
        Executable method,
        Map<String, Object> args
    ) {
        return this.parameterInjector.inject(this, method, args);
    }

    public Object propertyInjector(Object instance, Map<String, Object> args) {
        return this.propertyInjector.inject(this, instance, args);
    }

    public Object methodInjector(Object instance, Map<String, Object> args) {
        return this.methodInjector.inject(this, instance, args);
    }

    /* can remove */
    protected Stream<? extends Class<?>> getClassesFromBinding() {
        return this.bindings.keySet()
            .stream()
            .map(
                name -> {
                    try {
                        return Class.forName(name);
                    } catch (Throwable e) {
                        return null;
                    }
                }
            )
            .filter(Objects::nonNull);
    }

    public Set<Class<?>> getClassesByAnnotation(
        Class<? extends Annotation> annotation
    ) {
        return this.getClassesFromBinding()
            .filter(_class -> _class.isAnnotationPresent(annotation))
            .collect(Collectors.toSet());
    }

    public Set<Class<?>> getClassesBySuper(Class<?> superClass) {
        return this.getClassesFromBinding()
            .filter(superClass::isAssignableFrom)
            .filter(_class -> !_class.equals(superClass))
            .collect(Collectors.toSet());
    }
}
