package me.ixk.framework.ioc;

import cn.hutool.core.convert.Convert;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.DynamicInterceptor;
import me.ixk.framework.exceptions.ContainerException;
import me.ixk.framework.utils.ClassUtils;
import net.sf.cglib.proxy.Enhancer;

public class Container {
    protected Map<String, Binding> bindings;

    protected Map<String, Object> instances;

    protected Map<String, String> aliases;

    protected Map<String, Object> globalArgs = new ConcurrentHashMap<>();

    public Container() {
        this.bindings = new ConcurrentHashMap<>();
        this.instances = new ConcurrentHashMap<>();
        this.aliases = new ConcurrentHashMap<>();
    }

    /* bind String, String */

    public Container bind(String _abstract) {
        return this.bind(_abstract, _abstract);
    }

    public Container bind(String _abstract, String concrete) {
        return this.bind(_abstract, concrete, false);
    }

    public Container bind(String _abstract, String concrete, boolean shared) {
        return this.bind(_abstract, concrete, shared, null);
    }

    public Container bind(String _abstract, String concrete, String alias) {
        return this.bind(_abstract, concrete, false, alias);
    }

    public Container bind(
        String _abstract,
        String concrete,
        boolean shared,
        String alias
    ) {
        return this.bind(_abstract, concrete, shared, alias, false);
    }

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
        return this.bind(_abstract, concrete, false);
    }

    public Container bind(String _abstract, Concrete concrete, boolean shared) {
        return this.bind(_abstract, concrete, shared, null);
    }

    public Container bind(String _abstract, Concrete concrete, String alias) {
        return this.bind(_abstract, concrete, false, alias);
    }

    public Container bind(
        String _abstract,
        Concrete concrete,
        boolean shared,
        String alias
    ) {
        return this.bind(_abstract, concrete, shared, alias, false);
    }

    public Container bind(
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
        return this.bind(_abstract, concrete, false);
    }

    public Container bind(
        Class<?> _abstract,
        Concrete concrete,
        boolean shared
    ) {
        return this.bind(_abstract, concrete, shared, null);
    }

    public Container bind(Class<?> _abstract, Concrete concrete, String alias) {
        return this.bind(_abstract, concrete, false, alias);
    }

    public Container bind(
        Class<?> _abstract,
        Concrete concrete,
        boolean shared,
        String alias
    ) {
        return this.bind(_abstract, concrete, shared, alias, false);
    }

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
        return this.singleton(_abstract, _abstract);
    }

    public Container singleton(String _abstract, String concrete) {
        return this.singleton(_abstract, concrete, null);
    }

    public Container singleton(
        String _abstract,
        String concrete,
        String alias
    ) {
        return this.singleton(_abstract, concrete, alias, false);
    }

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
        return this.singleton(_abstract, concrete, null);
    }

    public Container singleton(
        String _abstract,
        Concrete concrete,
        String alias
    ) {
        return this.singleton(_abstract, concrete, alias, false);
    }

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
        return this.singleton(_abstract, _abstract);
    }

    public Container singleton(Class<?> _abstract, Class<?> concrete) {
        return this.singleton(_abstract, concrete, null);
    }

    public Container singleton(
        Class<?> _abstract,
        Class<?> concrete,
        String alias
    ) {
        return this.singleton(_abstract, concrete, alias, false);
    }

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
        return this.singleton(_abstract, concrete, null);
    }

    public Container singleton(
        Class<?> _abstract,
        Concrete concrete,
        String alias
    ) {
        return this.singleton(_abstract, concrete, alias, false);
    }

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

    public Container instance(String _abstract, Object instance, String alias) {
        String[] abstractAlias =
            this.getAbstractAndAliasByAlias(_abstract, alias);
        _abstract = abstractAlias[0];
        alias = abstractAlias[1];
        this.instances.put(_abstract, instance);
        this.bind(_abstract, (container, args) -> instance, true, alias, true);
        return this;
    }

    /* instance Class, Object */

    public Container instance(Class<?> _abstract, Object instance) {
        return this.instance(_abstract.getName(), instance, null);
    }

    public Container instance(
        Class<?> _abstract,
        Object instance,
        String alias
    ) {
        return this.instance(_abstract.getName(), instance, alias);
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
        //        if (binding == null) {
        //            throw new RuntimeException(
        //                "Target [" + _abstract + "] is not binding"
        //            );
        //        }
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

    public Object build(Concrete concrete, Map<String, Object> args) {
        try {
            return concrete.invoke(this, args);
        } catch (Throwable e) {
            throw new ContainerException("Instance build failed", e);
        }
    }

    public Object build(String _class, Map<String, Object> args) {
        try {
            return this.build(Class.forName(_class), args);
        } catch (ClassNotFoundException e) {
            throw new ContainerException("Instance build failed", e);
        }
    }

    public Object build(Class<?> _class, Map<String, Object> args) {
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
        Object instance = null;
        if (constructors.length == 1) {
            Constructor<?> constructor = constructors[0];
            Object[] dependencies =
                this.injectingDependencies(constructor, args);
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
        return this.injectingProperties(instance);
    }

    protected Object[] injectingParameters(
        Parameter[] parameters,
        Map<String, Object> args
    ) {
        Object[] dependencies = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (args.containsKey(parameter.getName())) {
                dependencies[i] = args.get(parameter.getName());
            } else {
                Class<?> _class = parameter.getType();
                dependencies[i] = this.make(_class.getName(), _class);
            }
            dependencies[i] =
                Convert.convert(parameter.getType(), dependencies[i]);
        }
        return dependencies;
    }

    protected Object[] injectingDependencies(
        Constructor<?> constructor,
        Map<String, Object> args
    ) {
        Parameter[] parameters = constructor.getParameters();
        return this.injectingParameters(parameters, args);
    }

    protected Object[] injectingDependencies(
        Method method,
        Map<String, Object> args
    ) {
        Parameter[] parameters = method.getParameters();
        return this.injectingParameters(parameters, args);
    }

    protected Object injectingProperties(Object instance) {
        if (instance == null) {
            return null;
        }
        Field[] fields = ClassUtils.getUserClass(instance).getDeclaredFields();
        for (Field field : fields) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired == null) {
                continue;
            }
            Object dependency;
            if (!autowired.name().equals("")) {
                dependency = this.make(autowired.name());
            } else {
                Class<?> autowiredClass = null;
                if (autowired.value() == Class.class) {
                    autowiredClass = field.getType();
                } else {
                    autowiredClass = autowired.value();
                }
                dependency = this.make(autowiredClass);
            }
            boolean originAccessible = field.isAccessible();
            field.setAccessible(true);
            try {
                field.set(instance, dependency);
            } catch (IllegalAccessException e) {
                throw new ContainerException("Object field setting failed", e);
            }
            field.setAccessible(originAccessible);
        }
        return instance;
    }

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
        _abstract = this.getAbstractByAlias(_abstract);
        Binding binding = this.getBinding(_abstract);
        if (binding.isShared() && this.instances.containsKey(_abstract)) {
            return returnType.cast(this.instances.get(_abstract));
        }
        Object instance = null;
        try {
            instance = binding.getConcrete().invoke(this, args);
        } catch (Throwable e) {
            throw new ContainerException("Instance make failed", e);
        }
        if (binding.isShared()) {
            this.instances.put(_abstract, instance);
        }
        return returnType.cast(instance);
    }

    public <T> T make(Class<T> _abstract) {
        return this.make(_abstract.getName(), _abstract, this.globalArgs);
    }

    public <T> T make(Class<T> _abstract, Map<String, Object> args) {
        return this.make(_abstract.getName(), _abstract, args);
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
        Object object = this.make(target[0], Object.class, newArgs);
        Method[] methods = Arrays
            .stream(object.getClass().getMethods())
            .filter(m -> m.getName().equals(target[1]))
            .toArray(Method[]::new);
        if (methods.length == 0) {
            throw new RuntimeException("The specified method was not found");
        } else if (methods.length > 1) {
            throw new RuntimeException(
                "The called method cannot be overloaded"
            );
        }
        return returnType.cast(this.callMethod(object, methods[0], args));
    }

    public <T> T call(String[] target, Class<T> returnType) {
        return this.call(target, returnType, this.globalArgs);
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
        Object object = this.make(target[0], Object.class, newArgs);
        Method method = null;
        try {
            method = object.getClass().getMethod(target[1], paramTypes);
        } catch (NoSuchMethodException e) {
            throw new ContainerException(
                "Corresponding methods not found in the specified class",
                e
            );
        }
        return returnType.cast(this.callMethod(object, method, args));
    }

    public <T> T call(
        String[] target,
        Class<?>[] paramTypes,
        Class<T> returnType
    ) {
        return this.call(target, paramTypes, returnType, this.globalArgs);
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

    public <T> T call(String target, Class<T> returnType) {
        return this.call(target, returnType, this.globalArgs);
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

    public <T> T call(
        String target,
        Class<?>[] paramTypes,
        Class<T> returnType
    ) {
        return this.call(target, paramTypes, returnType, this.globalArgs);
    }

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
        return returnType.cast(
            this.callMethod(this.make(_class, newArgs), method, args)
        );
    }

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

    protected Object callMethod(
        Object object,
        Method method,
        Map<String, Object> args
    ) {
        Object[] dependencies = this.injectingDependencies(method, args);
        try {
            return method.invoke(object, dependencies);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ContainerException("Method call failed", e);
        }
    }

    // Get
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

    public void remove(String _abstract) {
        String alias = _abstract;
        _abstract = this.getAbstractByAlias(_abstract);
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

    public void remove(Class<?> _abstract) {
        this.remove(_abstract.getName());
    }

    public Map<String, Object> getGlobalArgs() {
        return globalArgs;
    }

    public void setGlobalArgs(Map<String, Object> globalArgs) {
        this.globalArgs = globalArgs;
    }

    public void resetGlobalArgs() {
        this.globalArgs = new ConcurrentHashMap<>();
    }
}
