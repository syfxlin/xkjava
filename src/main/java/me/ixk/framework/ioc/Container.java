/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.ProxyCreator;
import me.ixk.framework.exceptions.ContainerException;
import me.ixk.framework.ioc.context.ContextName;
import me.ixk.framework.ioc.injector.DefaultMethodInjector;
import me.ixk.framework.ioc.injector.DefaultParameterInjector;
import me.ixk.framework.ioc.injector.DefaultPropertyInjector;
import me.ixk.framework.ioc.injector.PropertiesValueInjector;
import me.ixk.framework.ioc.processor.PostConstructProcessor;
import me.ixk.framework.ioc.processor.PreDestroyProcessor;
import me.ixk.framework.utils.ClassUtils;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.ParameterNameDiscoverer;
import me.ixk.framework.utils.ReflectUtils;

public class Container implements Context {
    // 各种注入器
    private final Map<Class<? extends ParameterInjector>, ParameterInjector> parameterInjectors = new LinkedHashMap<>();
    private final Map<Class<? extends InstanceInjector>, InstanceInjector> instanceInjectors = new LinkedHashMap<>();

    // 前置处理和后置处理，前置处理在初始化后进行，后置处理在删除前进行
    private final Map<Class<? extends BeanBeforeProcessor>, BeanBeforeProcessor> beanBeforeProcessors = new LinkedHashMap<>();
    private final Map<Class<? extends BeanAfterProcessor>, BeanAfterProcessor> beanAfterProcessors = new LinkedHashMap<>();

    // Contexts，存储实例和别名的空间
    private final Map<String, Context> contexts = Collections.synchronizedMap(
        new LinkedHashMap<>(5)
    );

    // 注入的临时变量
    private final ThreadLocal<DataBinder> dataBinder = new InheritableThreadLocal<>();

    // 构造器
    public Container() {
        this.dataBinder.set(
                new DefaultDataBinder(this, new ConcurrentHashMap<>())
            );

        this.addParameterInjector(new DefaultParameterInjector());
        this.addInstanceInjector(new DefaultPropertyInjector());
        this.addInstanceInjector(new DefaultMethodInjector());
        this.addInstanceInjector(new PropertiesValueInjector());

        this.addBeanBeforeProcessor(new PostConstructProcessor());
        this.addBeanAfterProcessor(new PreDestroyProcessor());
    }

    // 销毁方法
    public void destroy() {
        while (this.contexts.values().iterator().hasNext()) {
            Context context = this.contexts.values().iterator().next();
            this.removeContext(context);
        }
    }

    /* ===================== Base ===================== */

    @Override
    public String getName() {
        return ContextName.CONTAINER.getName();
    }

    @Override
    public boolean matchesScope(ScopeType scopeType) {
        return true;
    }

    public void registerContext(Context context) {
        this.contexts.put(context.getName(), context);
    }

    public void removeContext(String name) {
        Context context = this.contexts.get(name);
        this.removeContext(context);
    }

    public void removeContext(Context context) {
        if (context.isCreated()) {
            for (String name : context.getBindings().keySet()) {
                this.doRemove(name);
            }
        }
        this.contexts.remove(context.getName());
    }

    protected void walkContexts(Consumer<Context> consumer) {
        for (Context context : this.contexts.values()) {
            if (!context.isCreated()) {
                continue;
            }
            consumer.accept(context);
        }
    }

    protected <R> R walkContexts(Function<Context, R> supplier) {
        for (Context context : this.contexts.values()) {
            if (!context.isCreated()) {
                continue;
            }
            R result = supplier.apply(context);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void registerContexts(List<Context> contexts) {
        for (Context context : contexts) {
            this.registerContext(context);
        }
    }

    @Override
    public Map<String, String> getAliases() {
        Map<String, String> aliases = new ConcurrentHashMap<>();
        this.walkContexts(
                (Consumer<Context>) context ->
                    aliases.putAll(context.getAliases())
            );
        return aliases;
    }

    @Override
    public Map<String, Binding> getBindings() {
        Map<String, Binding> bindings = new ConcurrentHashMap<>();
        this.walkContexts(
                (Consumer<Context>) context ->
                    bindings.putAll(context.getBindings())
            );
        return bindings;
    }

    @Override
    public Binding getBinding(String name) {
        return this.walkContexts(
                (Function<Context, Binding>) context -> context.getBinding(name)
            );
    }

    public Binding getOrDefaultBinding(String name) {
        Binding binding = this.getBinding(name);
        if (binding == null) {
            binding = new Binding(null, ScopeType.PROTOTYPE, name);
            Binding finalBinding = binding;
            binding.setWrapper((container, with) -> this.doBuild(finalBinding));
        }
        return binding;
    }

    public Binding getOrDefaultBinding(Class<?> type) {
        return this.getOrDefaultBinding(type.getName());
    }

    @Override
    public void setBinding(String name, Binding binding) {
        this.getContextByBinding(binding).setBinding(name, binding);
    }

    @Override
    public boolean hasBinding(String name) {
        return (
            this.walkContexts(
                    context -> {
                        boolean has = context.hasBinding(name);
                        if (has) {
                            return true;
                        }
                        return null;
                    }
                ) !=
            null
        );
    }

    @Override
    public void removeBinding(String name) {
        this.walkContexts(
                (Consumer<Context>) context -> context.removeBinding(name)
            );
    }

    @Override
    @Deprecated
    public void registerAlias(String alias, String name) {
        throw new ContainerException("Do not call unregistered register alias");
    }

    public void registerAlias(String alias, String name, String contextName) {
        if (!this.contexts.containsKey(contextName)) {
            throw new ContainerException(
                "Target [" + contextName + "] context is not registered"
            );
        }
        this.getContextByName(contextName).registerAlias(alias, name);
    }

    @Override
    public void removeAlias(String alias) {
        this.walkContexts(
                (Consumer<Context>) context -> context.removeAlias(alias)
            );
    }

    @Override
    public boolean hasAlias(String alias) {
        return this.walkContexts(
                context -> {
                    boolean has = context.hasAlias(alias);
                    if (has) {
                        return true;
                    }
                    return null;
                }
            );
    }

    @Override
    public String getAlias(String alias) {
        return this.walkContexts(
                (Function<Context, String>) context -> context.getAlias(alias)
            );
    }

    @Override
    public String getCanonicalName(String name) {
        return this.walkContexts(
                (Function<Context, String>) context ->
                    context.getCanonicalName(name)
            );
    }

    public Context getContextByName(String contextName) {
        if (!this.contexts.containsKey(contextName)) {
            return null;
        }
        return this.contexts.get(contextName);
    }

    public String getContextNameByBinding(Binding binding) {
        for (Context context : this.contexts.values()) {
            if (context.matchesScope(binding.getScope())) {
                return context.getName();
            }
        }
        return null;
    }

    public Context getContextByBinding(Binding binding) {
        return this.getContextByName(this.getContextNameByBinding(binding));
    }

    public void setAttribute(
        String name,
        Object attribute,
        ScopeType scopeType
    ) {
        this.setBinding(
                Context.ATTRIBUTE_PREFIX + name,
                new Binding(attribute, scopeType)
            );
    }

    /* ===================== Base ===================== */

    protected void checkHasBinding(Class<?> bindType, boolean overwrite) {
        this.checkHasBinding(bindType.getName(), overwrite);
    }

    protected void checkHasBinding(String name, boolean overwrite) {
        if (!overwrite && this.hasBinding(name)) {
            throw new RuntimeException("Target [" + name + "] has been bind");
        }
    }

    protected Object processInstanceInjector(Binding binding, Object instance) {
        Class<?> instanceClass = ClassUtils.getUserClass(instance);
        for (InstanceInjector injector : this.instanceInjectors.values()) {
            instance =
                injector.process(
                    this,
                    binding,
                    instance,
                    instanceClass,
                    this.dataBinder.get()
                );
        }
        return instance;
    }

    protected Object[] processParameterInjector(
        Binding binding,
        Executable method
    ) {
        Object[] dependencies = new Object[method.getParameterCount()];
        method = ClassUtils.getUserMethod(method);
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = ParameterNameDiscoverer.getParameterNames(
            method
        );
        for (ParameterInjector injector : this.parameterInjectors.values()) {
            dependencies =
                injector.process(
                    this,
                    binding,
                    method,
                    parameters,
                    parameterNames,
                    dependencies,
                    this.dataBinder.get()
                );
        }
        return dependencies;
    }

    protected Object processBeanBefore(Binding binding, Object instance) {
        for (BeanBeforeProcessor processor : this.beanBeforeProcessors.values()) {
            instance = processor.process(this, binding, instance);
        }
        return instance;
    }

    protected Object processBeanAfter(Binding binding, Object instance) {
        for (BeanAfterProcessor processor : this.beanAfterProcessors.values()) {
            instance = processor.process(this, binding, instance);
        }
        return instance;
    }

    /* ===================== doBind ===================== */

    private synchronized Container doBind(
        String bindName,
        Binding binding,
        String alias
    ) {
        if (alias != null) {
            this.registerAlias(
                    alias,
                    bindName,
                    this.getContextNameByBinding(binding)
                );
        }
        this.setBinding(bindName, binding);
        return this;
    }

    protected synchronized Container doBind(
        String bindName,
        Wrapper wrapper,
        String alias,
        ScopeType scopeType,
        boolean overwrite
    ) {
        this.checkHasBinding(bindName, overwrite);
        Binding binding = new Binding(wrapper, scopeType, bindName);
        return this.doBind(bindName, binding, alias);
    }

    /* ===================== doInstance ===================== */

    protected synchronized Container doInstance(
        String instanceName,
        Object instance,
        String alias,
        ScopeType scopeType
    ) {
        Binding binding = this.getBinding(instanceName);
        if (binding != null) {
            throw new RuntimeException(
                "Target [" + instanceName + "] has been bind"
            );
        }
        binding = new Binding(instance, scopeType, instanceName);
        binding.setInstance(instance);
        this.doBind(instanceName, binding, alias);
        return this;
    }

    /* ===================== doBuild ===================== */

    protected synchronized Object doBuild(Binding binding) {
        Class<?> instanceType = binding.getInstanceType();
        if (instanceType == null) {
            return null;
        }
        // 排除 JDK 自带类的 doBuild
        if (ClassUtils.isSkipBuildType(instanceType)) {
            return ClassUtil.getDefaultValue(instanceType);
        }
        Constructor<?>[] constructors = ReflectUtils.sortConstructors(
            instanceType.getDeclaredConstructors()
        );
        Object instance;
        for (Constructor<?> constructor : constructors) {
            constructor.setAccessible(true);
            Object[] dependencies =
                this.processParameterInjector(binding, constructor);
            try {
                instance = constructor.newInstance(dependencies);
            } catch (Exception e) {
                continue;
            }
            instance = this.processInstanceInjector(binding, instance);
            if (
                !Advice.class.isAssignableFrom(instanceType) &&
                AspectManager.matches(instanceType)
            ) {
                instance =
                    ProxyCreator.createProxy(
                        instance,
                        instanceType,
                        instanceType.getInterfaces(),
                        constructor.getParameterTypes(),
                        dependencies
                    );
            }
            instance = this.processBeanBefore(binding, instance);
            if (instance != null) {
                return instance;
            }
        }
        return ClassUtil.getDefaultValue(instanceType);
    }

    /* ===================== doMake ===================== */

    protected synchronized <T> T doMake(
        String instanceName,
        Class<T> returnType
    ) {
        Binding binding = this.getOrDefaultBinding(instanceName);
        ScopeType scopeType = binding.getScope();
        Object instance =
            this.walkContexts(
                    context -> {
                        if (context.matchesScope(scopeType)) {
                            return context.getInstance(instanceName);
                        }
                        return null;
                    }
                );
        if (instance == null) {
            try {
                instance =
                    binding
                        .getWrapper()
                        .getInstance(this, this.dataBinder.get());
            } catch (Throwable e) {
                throw new ContainerException("Instance make failed", e);
            }
        }
        instance = ReflectUtils.resolveAutowiringValue(instance, returnType);
        T returnInstance = Convert.convert(returnType, instance);
        if (scopeType.isShared()) {
            this.walkContexts(
                    context -> {
                        if (
                            context.matchesScope(scopeType) &&
                            !context.hasInstance(instanceName)
                        ) {
                            context.setInstance(instanceName, returnInstance);
                        }
                    }
                );
        }
        return returnInstance;
    }

    /* ===================== doRemove ===================== */

    protected synchronized Container doRemove(String name) {
        Binding binding = this.getBinding(name);
        if (binding.isCreated()) {
            this.processBeanAfter(binding, binding.getInstance());
            this.removeBinding(name);
        }
        return this;
    }

    /* ===================== callMethod =============== */

    protected <T> T callMethod(
        Object instance,
        Method method,
        Class<T> returnType
    ) {
        Object[] dependencies = this.processParameterInjector(null, method);
        return Convert.convert(
            returnType,
            ReflectUtil.invoke(instance, method, dependencies)
        );
    }

    protected <T> T callMethod(
        Object instance,
        String methodName,
        Class<T> returnType
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
        return this.callMethod(instance, methods[0], returnType);
    }

    protected <T> T callMethod(
        String typeName,
        String methodName,
        Class<T> returnType
    ) {
        return this.callMethod(this.make(typeName), methodName, returnType);
    }

    protected <T> T callMethod(
        Class<?> type,
        String methodName,
        Class<T> returnType
    ) {
        return this.callMethod(type.getName(), methodName, returnType);
    }

    protected <T> T callMethod(
        String typeName,
        String methodName,
        Class<?>[] paramTypes,
        Class<T> returnType
    ) {
        Object instance = this.make(typeName);
        try {
            Method method = instance
                .getClass()
                .getMethod(methodName, paramTypes);
            return this.callMethod(instance, methodName, returnType);
        } catch (NoSuchMethodException e) {
            throw new ContainerException("Method not found");
        }
    }

    /* ===================== build ==================== */

    public Object build(Wrapper wrapper) {
        return this.doBuild(new Binding(wrapper, ScopeType.PROTOTYPE));
    }

    public Object build(String instanceName) {
        return this.doBuild(this.getOrDefaultBinding(instanceName));
    }

    public Object build(Class<?> instanceType) {
        return this.doBuild(this.getOrDefaultBinding(instanceType));
    }

    public Object build(Binding binding) {
        return this.doBuild(binding);
    }

    /* ===================== bind ===================== */

    // String
    // String, Wrapper

    public Container bind(String bindName) {
        return this.bind(
                bindName,
                (container, with) -> container.build(bindName)
            );
    }

    public Container bind(String bindName, Wrapper wrapper) {
        return this.bind(bindName, wrapper, null);
    }

    public Container bind(String bindName, Wrapper wrapper, String alias) {
        return this.bind(bindName, wrapper, alias, ScopeType.PROTOTYPE);
    }

    public Container bind(
        String bindName,
        Wrapper wrapper,
        String alias,
        ScopeType scopeType
    ) {
        return this.bind(bindName, wrapper, alias, scopeType, false);
    }

    public Container bind(
        String bindName,
        Wrapper wrapper,
        String alias,
        ScopeType scopeType,
        boolean overwrite
    ) {
        return this.doBind(bindName, wrapper, alias, scopeType, overwrite);
    }

    // Class
    // Class, Wrapper

    public Container bind(Class<?> bingType) {
        return this.bind(
                bingType,
                (container, with) -> container.build(bingType)
            );
    }

    public Container bind(Class<?> bingType, Wrapper wrapper) {
        return this.bind(bingType, wrapper, null);
    }

    public Container bind(Class<?> bingType, Wrapper wrapper, String alias) {
        return this.bind(bingType, wrapper, alias, ScopeType.PROTOTYPE);
    }

    public Container bind(
        Class<?> bingType,
        Wrapper wrapper,
        String alias,
        ScopeType scopeType
    ) {
        return this.bind(bingType, wrapper, alias, scopeType, false);
    }

    public Container bind(
        Class<?> bindType,
        Wrapper wrapper,
        String alias,
        ScopeType scopeType,
        boolean overwrite
    ) {
        return this.doBind(
                bindType.getName(),
                wrapper,
                alias,
                scopeType,
                overwrite
            );
    }

    // String, String

    public Container bind(String bindName, String wrapper) {
        return this.bind(bindName, wrapper, null);
    }

    public Container bind(String bindName, String wrapper, String alias) {
        return this.bind(bindName, wrapper, alias, ScopeType.PROTOTYPE);
    }

    public Container bind(
        String bindName,
        String wrapper,
        String alias,
        ScopeType scopeType
    ) {
        return this.bind(bindName, wrapper, alias, scopeType, false);
    }

    public Container bind(
        String bindName,
        String wrapper,
        String alias,
        ScopeType scopeType,
        boolean overwrite
    ) {
        return this.doBind(
                bindName,
                (container, with) -> this.build(wrapper),
                alias,
                scopeType,
                overwrite
            );
    }

    // Class, Class

    public Container bind(Class<?> bindType, Class<?> wrapper) {
        return this.bind(bindType, wrapper, null);
    }

    public Container bind(Class<?> bindType, Class<?> wrapper, String alias) {
        return this.bind(bindType, wrapper, alias, ScopeType.PROTOTYPE);
    }

    public Container bind(
        Class<?> bindType,
        Class<?> wrapper,
        String alias,
        ScopeType scopeType
    ) {
        return this.bind(bindType, wrapper, alias, scopeType, false);
    }

    public Container bind(
        Class<?> bindType,
        Class<?> wrapper,
        String alias,
        ScopeType scopeType,
        boolean overwrite
    ) {
        return this.doBind(
                bindType.getName(),
                (container, with) -> this.build(wrapper),
                alias,
                scopeType,
                overwrite
            );
    }

    /* ==================== singleton ====================== */

    public Container singleton(String bindName) {
        return this.singleton(bindName, bindName);
    }

    public Container singleton(String bindName, String wrapper) {
        return this.singleton(bindName, wrapper, null);
    }

    public Container singleton(String bindName, String wrapper, String alias) {
        return this.singleton(bindName, wrapper, alias, false);
    }

    public Container singleton(
        String bindName,
        String wrapper,
        String alias,
        boolean overwrite
    ) {
        return this.bind(
                bindName,
                wrapper,
                alias,
                ScopeType.SINGLETON,
                overwrite
            );
    }

    public Container singleton(String bindName, Wrapper wrapper) {
        return this.singleton(bindName, wrapper, null);
    }

    public Container singleton(String bindName, Wrapper wrapper, String alias) {
        return this.singleton(bindName, wrapper, alias, false);
    }

    public Container singleton(
        String bindName,
        Wrapper wrapper,
        String alias,
        boolean overwrite
    ) {
        return this.bind(
                bindName,
                wrapper,
                alias,
                ScopeType.SINGLETON,
                overwrite
            );
    }

    public Container singleton(Class<?> bindType) {
        return this.singleton(bindType, bindType);
    }

    public Container singleton(Class<?> bindType, Class<?> wrapper) {
        return this.singleton(bindType, wrapper, null);
    }

    public Container singleton(
        Class<?> bindType,
        Class<?> wrapper,
        String alias
    ) {
        return this.singleton(bindType, wrapper, alias, false);
    }

    public Container singleton(
        Class<?> bindType,
        Class<?> wrapper,
        String alias,
        boolean overwrite
    ) {
        return this.bind(
                bindType,
                wrapper,
                alias,
                ScopeType.SINGLETON,
                overwrite
            );
    }

    public Container singleton(Class<?> bindType, Wrapper wrapper) {
        return this.singleton(bindType, wrapper, null);
    }

    public Container singleton(
        Class<?> bindType,
        Wrapper wrapper,
        String alias
    ) {
        return this.singleton(bindType, wrapper, alias, false);
    }

    public Container singleton(
        Class<?> bindType,
        Wrapper wrapper,
        String alias,
        boolean overwrite
    ) {
        return this.bind(
                bindType,
                wrapper,
                alias,
                ScopeType.SINGLETON,
                overwrite
            );
    }

    /* ======================= instance =========================== */

    public Container instance(String bindName, Object instance) {
        return this.instance(bindName, instance, null, ScopeType.SINGLETON);
    }

    public Container instance(
        String bindName,
        Object instance,
        ScopeType scopeType
    ) {
        return this.instance(bindName, instance, null, scopeType);
    }

    public Container instance(String bindName, Object instance, String alias) {
        return this.doInstance(bindName, instance, alias, ScopeType.SINGLETON);
    }

    public Container instance(
        String bindName,
        Object instance,
        String alias,
        ScopeType scopeType
    ) {
        return this.doInstance(bindName, instance, alias, scopeType);
    }

    public Container instance(Class<?> bindType, Object instance) {
        return this.instance(
                bindType.getName(),
                instance,
                null,
                ScopeType.SINGLETON
            );
    }

    public Container instance(
        Class<?> bindType,
        Object instance,
        ScopeType scopeType
    ) {
        return this.instance(bindType.getName(), instance, null, scopeType);
    }

    public Container instance(
        Class<?> bindType,
        Object instance,
        String alias
    ) {
        return this.instance(
                bindType.getName(),
                instance,
                alias,
                ScopeType.SINGLETON
            );
    }

    public Container instance(
        Class<?> bindType,
        Object instance,
        String alias,
        ScopeType scopeType
    ) {
        return this.instance(bindType.getName(), instance, alias, scopeType);
    }

    /* ======================= make =========================== */

    public Object make(String bindName) {
        return this.make(bindName, Object.class);
    }

    public <T> T make(String bindName, Class<T> returnType) {
        return this.make(bindName, returnType, this.dataBinder.get());
    }

    public <T> T make(
        String bindName,
        Class<T> returnType,
        DataBinder dataBinder
    ) {
        return this.withAndReset(
                () -> this.doMake(bindName, returnType),
                dataBinder
            );
    }

    public <T> T make(
        String bindName,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.make(
                bindName,
                returnType,
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T make(Class<T> bindType) {
        return this.make(bindType.getName(), bindType, this.dataBinder.get());
    }

    public <T> T make(Class<T> bindType, Map<String, Object> args) {
        return this.make(bindType.getName(), bindType, args);
    }

    public <T> T make(Class<T> bindType, DataBinder dataBinder) {
        return this.make(bindType.getName(), bindType, dataBinder);
    }

    /* ====================== remove ======================= */

    public Container remove(String name) {
        return this.doRemove(name);
    }

    public Container remove(Class<?> type) {
        return this.doRemove(type.getName());
    }

    /* ====================== call ========================= */

    public <T> T call(String[] target, Class<T> returnType) {
        if (target.length != 2) {
            throw new ContainerException(
                "The length of the target array must be 2"
            );
        }
        return this.callMethod(target[0], target[1], returnType);
    }

    public <T> T call(
        String[] target,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.withAndReset(
                () -> this.call(target, returnType),
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T call(
        String[] target,
        Class<?>[] paramTypes,
        Class<T> returnType
    ) {
        if (target.length != 2) {
            throw new ContainerException(
                "The length of the target array must be 2"
            );
        }
        return this.callMethod(target[0], target[1], paramTypes, returnType);
    }

    public <T> T call(
        String[] target,
        Class<?>[] paramTypes,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.withAndReset(
                () -> this.call(target, paramTypes, returnType),
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T call(String target, Class<T> returnType) {
        return this.call(target.split("@"), returnType);
    }

    public <T> T call(
        String target,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(target.split("@"), returnType, args);
    }

    public <T> T call(
        String target,
        Class<?>[] paramTypes,
        Class<T> returnType
    ) {
        return this.call(target.split("@"), paramTypes, returnType);
    }

    public <T> T call(
        String target,
        Class<?>[] paramTypes,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(target.split("@"), paramTypes, returnType, args);
    }

    public <T> T call(Class<?> type, Method method, Class<T> returnType) {
        return this.callMethod(this.make(type), method, returnType);
    }

    public <T> T call(
        Class<?> type,
        Method method,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(this.make(type), method, returnType, args);
    }

    public <T> T call(Method method, Class<T> returnType) {
        return this.call(method.getDeclaringClass(), method, returnType);
    }

    public <T> T call(
        Method method,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(method.getDeclaringClass(), method, returnType, args);
    }

    public <T> T call(Object instance, Method method, Class<T> returnType) {
        return this.callMethod(instance, method, returnType);
    }

    public <T> T call(
        Object instance,
        Method method,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(
                instance,
                method,
                returnType,
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T call(
        Object instance,
        Method method,
        Class<T> returnType,
        DataBinder binder
    ) {
        return this.withAndReset(
                () -> this.callMethod(instance, method, returnType),
                binder
            );
    }

    public <T> T call(Object instance, String methodName, Class<T> returnType) {
        return this.callMethod(instance, methodName, returnType);
    }

    public <T> T call(
        Object instance,
        String methodName,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(
                instance,
                methodName,
                returnType,
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T call(
        Object instance,
        String methodName,
        Class<T> returnType,
        DataBinder dataBinder
    ) {
        return this.withAndReset(
                () -> this.callMethod(instance, methodName, returnType),
                dataBinder
            );
    }

    public <T> T call(Class<?> type, String methodName, Class<T> returnType) {
        return this.callMethod(type, methodName, returnType);
    }

    public <T> T call(
        Class<?> type,
        String methodName,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return this.call(
                type,
                methodName,
                returnType,
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T call(
        Class<?> type,
        String methodName,
        Class<T> returnType,
        DataBinder binder
    ) {
        return this.withAndReset(
                () -> this.call(type, methodName, returnType),
                binder
            );
    }

    /* ===================================================== */

    public DataBinder getDataBinder() {
        return this.dataBinder.get();
    }

    public Container with(Map<String, Object> args) {
        return this.with(null, args);
    }

    public Container with(String prefix, Map<String, Object> args) {
        this.dataBinder.set(new DefaultDataBinder(this, args));
        return this;
    }

    public Container resetWith() {
        this.dataBinder.set(
                new DefaultDataBinder(this, new ConcurrentHashMap<>())
            );
        return this;
    }

    public <T> T withAndReset(Supplier<T> callback, DataBinder dataBinder) {
        DataBinder reset = this.dataBinder.get();
        this.dataBinder.set(dataBinder);
        T result = callback.get();
        this.dataBinder.set(reset);
        return result;
    }

    public Map<String, Context> getContexts() {
        return contexts;
    }

    public Container addInstanceInjector(InstanceInjector injector) {
        this.instanceInjectors.put(injector.getClass(), injector);
        return this;
    }

    public Container removeInstanceInjector(
        Class<? extends InstanceInjector> injector
    ) {
        this.instanceInjectors.remove(injector);
        return this;
    }

    public Map<Class<? extends InstanceInjector>, InstanceInjector> getInstanceInjectors() {
        return instanceInjectors;
    }

    public Container addParameterInjector(ParameterInjector injector) {
        this.parameterInjectors.put(injector.getClass(), injector);
        return this;
    }

    public Container removeParameterInjector(
        Class<? extends ParameterInjector> injector
    ) {
        this.parameterInjectors.remove(injector);
        return this;
    }

    public Map<Class<? extends ParameterInjector>, ParameterInjector> getParameterInjectors() {
        return parameterInjectors;
    }

    public Container addBeanBeforeProcessor(BeanBeforeProcessor processor) {
        this.beanBeforeProcessors.put(processor.getClass(), processor);
        return this;
    }

    public Container removeBeanBeforeProcessor(
        Class<? extends BeanBeforeProcessor> processor
    ) {
        this.beanBeforeProcessors.remove(processor);
        return this;
    }

    public Map<Class<? extends BeanBeforeProcessor>, BeanBeforeProcessor> getBeanBeforeProcessors() {
        return beanBeforeProcessors;
    }

    public Container addBeanAfterProcessor(BeanAfterProcessor processor) {
        this.beanAfterProcessors.put(processor.getClass(), processor);
        return this;
    }

    public Container removeBeanAfterProcessor(
        Class<? extends BeanAfterProcessor> processor
    ) {
        this.beanAfterProcessors.remove(processor);
        return this;
    }

    public Map<Class<? extends BeanAfterProcessor>, BeanAfterProcessor> getBeanAfterProcessors() {
        return beanAfterProcessors;
    }
}
