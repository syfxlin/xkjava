package me.ixk.framework.ioc;

import cn.hutool.core.convert.Convert;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.DynamicInterceptor;
import me.ixk.framework.exceptions.ContainerException;
import me.ixk.framework.factory.AfterInitProcessor;
import me.ixk.framework.factory.ObjectFactory;
import me.ixk.framework.ioc.injector.DefaultMethodInjector;
import me.ixk.framework.ioc.injector.DefaultParameterInjector;
import me.ixk.framework.ioc.injector.DefaultPropertyInjector;
import me.ixk.framework.utils.AutowireUtils;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Container implements Attributes {
    protected final ParameterInjector parameterInjector = new DefaultParameterInjector();
    protected final PropertyInjector propertyInjector = new DefaultPropertyInjector();
    protected final MethodInjector methodInjector = new DefaultMethodInjector();

    protected final Map<String, Binding> bindings = new ConcurrentHashMap<>();

    protected final Map<String, String> aliases = new ConcurrentHashMap<>();

    protected final Map<String, Object> instance = new ConcurrentHashMap<>();

    protected Map<String, Object> globalArgs = new ConcurrentHashMap<>();

    protected Map<String, Object> resetGlobalArgs;

    protected List<AfterInitProcessor> afterInitProcessors = new ArrayList<>();

    /* base */

    @Override
    public boolean hasAttribute(String name) {
        return this.instance.containsKey(name);
    }

    @Override
    public Object getAttribute(String name) {
        return this.instance.get(name);
    }

    @Override
    public void setAttribute(String name, Object attribute) {
        this.instance.put(name, attribute);
    }

    @Override
    public void removeAttribute(String name) {
        this.instance.remove(name);
    }

    @Override
    public String[] getAttributeNames() {
        return this.instance.keySet().toArray(new String[0]);
    }

    public ParameterInjector getParameterInjector() {
        return parameterInjector;
    }

    public PropertyInjector getPropertyInjector() {
        return propertyInjector;
    }

    public MethodInjector getMethodInjector() {
        return methodInjector;
    }

    public void createRequestContext() {
        RequestContext.create();
    }

    public void removeRequestContext() {
        RequestContext.removeAttributes();
    }

    public RequestContext getRequestContext() {
        return RequestContext.currentAttributes();
    }

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
        ScopeType scopeType,
        boolean overwrite
    ) {
        _abstract = this.getAbstractByAlias(_abstract);
        Concrete concrete1 = (container, args) ->
            container.build(concrete, args);
        this.setBinding(_abstract, concrete1, scopeType, overwrite);
    }

    protected void setBinding(
        String _abstract,
        Concrete concrete,
        ScopeType scopeType,
        boolean overwrite
    ) {
        if (
            !overwrite &&
            scopeType.isShared() &&
            this.bindings.containsKey(_abstract)
        ) {
            throw new RuntimeException(
                "Target [" + _abstract + "] is a singleton and has been bind"
            );
        }
        this.bindings.put(_abstract, new Binding(concrete, scopeType));
    }

    protected Binding getBinding(String _abstract) {
        _abstract = this.getAbstractByAlias(_abstract);
        String finalAbstract = _abstract;
        // 自动创建binding
        return this.bindings.getOrDefault(
                _abstract,
                new Binding(
                    (container, args) -> container.build(finalAbstract, args),
                    ScopeType.PROTOTYPE
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

    protected String getAbstractByAlias(String name) {
        String _abstract = name;
        String resolved;
        do {
            resolved = this.aliases.get(_abstract);
            if (resolved != null) {
                _abstract = resolved;
            }
        } while (resolved != null);
        return _abstract;
    }

    /* doing */

    protected Container doBind(
        String _abstract,
        Concrete concrete,
        ScopeType scopeType,
        String alias,
        boolean overwrite
    ) {
        this.setBinding(_abstract, concrete, scopeType, overwrite);
        if (alias != null) {
            this.alias(alias, _abstract);
        }
        return this;
    }

    protected Container doInstance(
        String _abstract,
        Object instance,
        String alias,
        ScopeType scopeType
    ) {
        String[] abstractAlias =
            this.getAbstractAndAliasByAlias(_abstract, alias);
        _abstract = abstractAlias[0];
        alias = abstractAlias[1];
        // 处理 Set 注入和方法
        instance = this.methodInjector.inject(this, instance, this.globalArgs);
        if (scopeType.isRequest()) {
            this.getRequestContext().setAttribute(_abstract, instance);
        } else {
            this.setAttribute(_abstract, instance);
        }
        Object finalInstance = instance;
        this.bind(
                _abstract,
                (container, args) -> finalInstance,
                scopeType,
                alias,
                true
            );
        return this;
    }

    protected Object doBuild(Class<?> _class, Map<String, Object> args) {
        Map<String, List<Advice>> map = null;
        if (this.hasAttribute(AspectManager.class.getName())) {
            try {
                map =
                    (
                        (AspectManager) this.getAttribute(
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
        ScopeType scopeType = binding.getType();
        if (
            scopeType.isRequest() &&
            this.getRequestContext().hasAttribute(_abstract)
        ) {
            String final_abstract = _abstract;
            instance =
                (ObjectFactory<Object>) () ->
                    this.getRequestContext().getAttribute(final_abstract);
        } else if (binding.isShared() && this.hasAttribute(_abstract)) {
            instance = this.getAttribute(_abstract);
        } else {
            try {
                instance = binding.getConcrete().getObject(this, args);
            } catch (Throwable e) {
                throw new ContainerException("Instance make failed", e);
            }
        }
        // 解决动态注入
        instance = AutowireUtils.resolveAutowiringValue(instance, returnType);
        instance = this.doAfterProcessor(instance, returnType);
        instance = Convert.convert(returnType, instance);
        if (scopeType.isRequest()) {
            if (!this.getRequestContext().hasAttribute(_abstract)) {
                this.getRequestContext().setAttribute(_abstract, instance);
            }
        } else if (scopeType.isShared()) {
            if (!this.hasAttribute(_abstract)) {
                this.setAttribute(_abstract, instance);
            }
        }
        return returnType.cast(instance);
    }

    protected void doRemove(String _abstract) {
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
        this.removeAttribute(_abstract);
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
        return this.bind(
                _abstract,
                _abstract,
                ScopeType.PROTOTYPE,
                null,
                false
            );
    }

    public Container bind(String _abstract, String concrete) {
        return this.bind(_abstract, concrete, ScopeType.PROTOTYPE, null, false);
    }

    public Container bind(
        String _abstract,
        String concrete,
        ScopeType scopeType
    ) {
        return this.bind(_abstract, concrete, scopeType, null, false);
    }

    public Container bind(String _abstract, String concrete, String alias) {
        return this.bind(
                _abstract,
                concrete,
                ScopeType.PROTOTYPE,
                alias,
                false
            );
    }

    public Container bind(
        String _abstract,
        String concrete,
        ScopeType scopeType,
        String alias
    ) {
        return this.bind(_abstract, concrete, scopeType, alias, false);
    }

    /* bind final String, String */
    public Container bind(
        String _abstract,
        String concrete,
        ScopeType scopeType,
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
                scopeType,
                alias,
                overwrite
            );
    }

    /* bind String, Concrete */

    public Container bind(String _abstract, Concrete concrete) {
        return this.bind(_abstract, concrete, ScopeType.PROTOTYPE, null, false);
    }

    public Container bind(
        String _abstract,
        Concrete concrete,
        ScopeType scopeType
    ) {
        return this.bind(_abstract, concrete, scopeType, null, false);
    }

    public Container bind(String _abstract, Concrete concrete, String alias) {
        return this.bind(
                _abstract,
                concrete,
                ScopeType.PROTOTYPE,
                alias,
                false
            );
    }

    public Container bind(
        String _abstract,
        Concrete concrete,
        ScopeType scopeType,
        String alias
    ) {
        return this.bind(_abstract, concrete, scopeType, alias, false);
    }

    /* bind final String, Concrete */
    public Container bind(
        String _abstract,
        Concrete concrete,
        ScopeType scopeType,
        String alias,
        boolean overwrite
    ) {
        return this.doBind(_abstract, concrete, scopeType, alias, overwrite);
    }

    /* bind Class, Class */

    public Container bind(Class<?> _abstract) {
        return this.bind(
                _abstract,
                _abstract,
                ScopeType.PROTOTYPE,
                null,
                false
            );
    }

    public Container bind(Class<?> _abstract, Class<?> concrete) {
        return this.bind(_abstract, concrete, ScopeType.PROTOTYPE, null, false);
    }

    public Container bind(
        Class<?> _abstract,
        Class<?> concrete,
        ScopeType scopeType
    ) {
        return this.bind(_abstract, concrete, scopeType, null, false);
    }

    public Container bind(
        Class<?> _abstract,
        Class<?> concrete,
        ScopeType scopeType,
        String alias
    ) {
        return this.bind(_abstract, concrete, scopeType, alias, false);
    }

    /* bind final Class, Class */
    public Container bind(
        Class<?> _abstract,
        Class<?> concrete,
        ScopeType scopeType,
        String alias,
        boolean overwrite
    ) {
        return this.bind(
                _abstract.getName(),
                concrete.getName(),
                scopeType,
                alias,
                overwrite
            );
    }

    /* bind Class, Concrete */

    public Container bind(Class<?> _abstract, Concrete concrete) {
        return this.bind(_abstract, concrete, ScopeType.PROTOTYPE, null, false);
    }

    public Container bind(
        Class<?> _abstract,
        Concrete concrete,
        ScopeType scopeType
    ) {
        return this.bind(_abstract, concrete, scopeType, null, false);
    }

    public Container bind(Class<?> _abstract, Concrete concrete, String alias) {
        return this.bind(
                _abstract,
                concrete,
                ScopeType.PROTOTYPE,
                alias,
                false
            );
    }

    public Container bind(
        Class<?> _abstract,
        Concrete concrete,
        ScopeType scopeType,
        String alias
    ) {
        return this.bind(_abstract, concrete, scopeType, alias, false);
    }

    /* bind final Class, Concrete */
    public Container bind(
        Class<?> _abstract,
        Concrete concrete,
        ScopeType scopeType,
        String alias,
        boolean overwrite
    ) {
        return this.bind(
                _abstract.getName(),
                concrete,
                scopeType,
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
        return this.bind(
                _abstract,
                concrete,
                ScopeType.SINGLETON,
                alias,
                overwrite
            );
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
        return this.bind(
                _abstract,
                concrete,
                ScopeType.SINGLETON,
                alias,
                overwrite
            );
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
        return this.bind(
                _abstract,
                concrete,
                ScopeType.SINGLETON,
                alias,
                overwrite
            );
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
        return this.bind(
                _abstract,
                concrete,
                ScopeType.SINGLETON,
                alias,
                overwrite
            );
    }

    /* instance String, Object */

    public Container instance(String _abstract, Object instance) {
        return this.instance(_abstract, instance, null, ScopeType.SINGLETON);
    }

    public Container instance(
        String _abstract,
        Object instance,
        ScopeType scopeType
    ) {
        return this.instance(_abstract, instance, null, scopeType);
    }

    /* instance final String, Object */

    public Container instance(String _abstract, Object instance, String alias) {
        return this.doInstance(_abstract, instance, alias, ScopeType.SINGLETON);
    }

    public Container instance(
        String _abstract,
        Object instance,
        String alias,
        ScopeType scopeType
    ) {
        return this.doInstance(_abstract, instance, alias, scopeType);
    }

    /* instance Class, Object */

    public Container instance(Class<?> _abstract, Object instance) {
        return this.instance(
                _abstract.getName(),
                instance,
                null,
                ScopeType.SINGLETON
            );
    }

    public Container instance(
        Class<?> _abstract,
        Object instance,
        ScopeType scopeType
    ) {
        return this.instance(_abstract.getName(), instance, null, scopeType);
    }

    /* instance final Class, Object */

    public Container instance(
        Class<?> _abstract,
        Object instance,
        String alias
    ) {
        return this.instance(
                _abstract.getName(),
                instance,
                alias,
                ScopeType.SINGLETON
            );
    }

    public Container instance(
        Class<?> _abstract,
        Object instance,
        String alias,
        ScopeType scopeType
    ) {
        return this.instance(_abstract.getName(), instance, alias, scopeType);
    }

    /* build Concrete */

    public Object build(Concrete concrete, Map<String, Object> args) {
        try {
            return concrete.getObject(this, args);
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
}
