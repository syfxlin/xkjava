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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.ProxyCreator;
import me.ixk.framework.bootstrap.LoadEnvironmentVariables;
import me.ixk.framework.exceptions.ContainerException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IoC 容器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:17
 */
public class Container {
    private static final Logger log = LoggerFactory.getLogger(Container.class);

    private static final int ARRAY_METHOD_DEF_LENGTH = 2;
    private static final String ATTRIBUTE_PREFIX = "$_";
    /**
     * 参数注入器
     */
    private final Map<Class<? extends ParameterInjector>, ParameterInjector> parameterInjectors = new LinkedHashMap<>();

    /**
     * 实例注入器
     */
    private final Map<Class<? extends InstanceInjector>, InstanceInjector> instanceInjectors = new LinkedHashMap<>();

    /**
     * 前置处理器，在初始化后进行
     */
    private final Map<Class<? extends BeanBeforeProcessor>, BeanBeforeProcessor> beanBeforeProcessors = new LinkedHashMap<>();
    /**
     * 后置处理,在删除前进行
     */
    private final Map<Class<? extends BeanAfterProcessor>, BeanAfterProcessor> beanAfterProcessors = new LinkedHashMap<>();

    /**
     * Contexts，存储实例的空间
     */
    private final Map<Class<? extends Context>, Context> contexts = Collections.synchronizedMap(
        new LinkedHashMap<>(5)
    );

    /**
     * Bindings
     */
    private final Map<String, Binding> bindings = new ConcurrentHashMap<>(256);

    /**
     * 别名
     */
    private final Map<String, String> aliases = new ConcurrentHashMap<>(256);

    /**
     * 注入的临时变量
     */
    private final ThreadLocal<DataBinder> dataBinder = new InheritableThreadLocal<>();

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

        log.info("Container created");
    }

    /**
     * 销毁方法
     */
    public void destroy() {
        while (this.contexts.values().iterator().hasNext()) {
            final Context context = this.contexts.values().iterator().next();
            this.removeContext(context);
        }
        log.info("Container destroyed");
    }

    /* ===================== Base ===================== */

    public Map<String, String> getAliases() {
        return aliases;
    }

    public Map<String, Binding> getBindings() {
        return bindings;
    }

    /* ===================== Context ===================== */

    public void registerContext(final Context context) {
        final Class<? extends Context> contextType = context.getClass();
        log.debug("Container registered context: {}", contextType.getName());
        this.contexts.put(contextType, context);
    }

    public void removeContext(final Class<? extends Context> contextType) {
        final Context context = this.contexts.get(contextType);
        this.removeContext(context);
    }

    public void removeContext(final Context context) {
        final Class<? extends Context> contextType = context.getClass();
        log.debug("Container remove context: {}", contextType.getName());
        if (context.isCreated()) {
            for (final Entry<String, Binding> entry : this.bindings.entrySet()) {
                if (context.matchesScope(entry.getValue().getScope())) {
                    this.doRemove(entry.getKey());
                }
            }
        }
        this.contexts.remove(contextType);
    }

    public void registerContexts(final List<Context> contexts) {
        for (final Context context : contexts) {
            this.registerContext(context);
        }
    }

    public Context getContextByScope(final ScopeType scopeType) {
        for (final Context context : this.contexts.values()) {
            if (context.matchesScope(scopeType)) {
                return context;
            }
        }
        return null;
    }

    public Context getContext(final Class<? extends Context> contextType) {
        return this.contexts.get(contextType);
    }

    /* ===================== Binding ===================== */

    private Binding newBinding(
        final String name,
        final Wrapper wrapper,
        final ScopeType scopeType
    ) {
        return new Binding(
            this.getContextByScope(scopeType),
            name,
            wrapper,
            scopeType
        );
    }

    private Binding newBinding(
        final String name,
        final Object instance,
        final ScopeType scopeType
    ) {
        return new Binding(
            this.getContextByScope(scopeType),
            name,
            instance,
            scopeType
        );
    }

    public Binding getBinding(final String name) {
        return this.bindings.get(this.getCanonicalName(name));
    }

    public Binding getOrDefaultBinding(final String name) {
        Binding binding = this.getBinding(name);
        if (binding == null) {
            binding = this.newBinding(name, null, ScopeType.PROTOTYPE);
            final Binding finalBinding = binding;
            binding.setWrapper((container, with) -> this.doBuild(finalBinding));
        }
        return binding;
    }

    public Binding getOrDefaultBinding(final Class<?> type) {
        return this.getOrDefaultBinding(type.getName());
    }

    public Binding setBinding(final String name, final Binding binding) {
        log.debug("Container set binding: {}", name);
        this.bindings.put(this.getCanonicalName(name), binding);
        return binding;
    }

    public boolean hasBinding(final String name) {
        return this.bindings.containsKey(this.getCanonicalName(name));
    }

    public boolean hasBinding(final Class<?> type) {
        return this.hasBinding(type.getName());
    }

    public void removeBinding(final String name) {
        log.debug("Container remove binding: {}", name);
        this.bindings.remove(this.getCanonicalName(name));
    }

    protected void checkHasBinding(final String name, final boolean overwrite) {
        if (!overwrite && this.hasBinding(name)) {
            throw new IllegalStateException(
                "Target [" + name + "] has been bind"
            );
        }
    }

    public void setInstanceValue(final String name, final Object instance) {
        final Binding binding = this.getBinding(name);
        if (binding == null) {
            throw new NullPointerException(
                "Target [" + name + "] not been bind"
            );
        }
        binding.setInstance(instance);
    }

    public void setInstanceValue(final Class<?> type, final Object instance) {
        this.setInstanceValue(type.getName(), instance);
    }

    /* ===================== Alias ===================== */

    private String getCanonicalName(final String name) {
        String canonicalName = name;
        String resolvedName;
        do {
            resolvedName = this.getAlias(canonicalName);
            if (resolvedName != null) {
                if (name.equals(resolvedName)) {
                    break;
                }
                canonicalName = resolvedName;
            }
        } while (resolvedName != null);
        return canonicalName;
    }

    public void setAlias(final String alias, final String name) {
        if (alias == null || alias.equals(name)) {
            return;
        }
        log.debug("Container add alias: {} => {}", alias, name);
        if (this.aliases.containsKey(alias)) {
            throw new IllegalStateException(
                "Alias [" + alias + "] has contains"
            );
        }
        this.aliases.put(alias, name);
    }

    public void removeAlias(final String alias) {
        log.debug("Container remove alias: {}", alias);
        this.aliases.remove(alias);
    }

    public boolean hasAlias(final String alias) {
        return this.aliases.containsKey(alias);
    }

    public String getAlias(final String alias) {
        return this.aliases.get(alias);
    }

    /*======================  Attribute  ==================*/

    public void removeAttribute(final String name) {
        for (final Context context : this.contexts.values()) {
            if (context.has(ATTRIBUTE_PREFIX + name)) {
                context.remove(ATTRIBUTE_PREFIX + name);
                return;
            }
        }
    }

    public Object getAttribute(final String name) {
        for (final Context context : this.contexts.values()) {
            if (context.has(ATTRIBUTE_PREFIX + name)) {
                return context.get(ATTRIBUTE_PREFIX + name);
            }
        }
        return null;
    }

    public boolean hasAttribute(final String name) {
        return this.getAttribute(name) != null;
    }

    public void setAttribute(
        final String name,
        final Object attribute,
        final ScopeType scopeType
    ) {
        this.getContextByScope(scopeType)
            .set(ATTRIBUTE_PREFIX + name, attribute);
    }

    public void removeAttribute(final String name, final ScopeType scopeType) {
        this.getContextByScope(scopeType).remove(ATTRIBUTE_PREFIX + name);
    }

    public Object getAttribute(final String name, final ScopeType scopeType) {
        return this.getContextByScope(scopeType).get(ATTRIBUTE_PREFIX + name);
    }

    public boolean hasAttribute(final String name, final ScopeType scopeType) {
        return this.getAttribute(name, scopeType) != null;
    }

    /* ===================== Process ===================== */

    protected Object processInstanceInjector(
        final Binding binding,
        Object instance
    ) {
        final Class<?> instanceClass = ClassUtils.getUserClass(instance);
        for (final InstanceInjector injector : this.instanceInjectors.values()) {
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
        final Binding binding,
        Executable method
    ) {
        Object[] dependencies = new Object[method.getParameterCount()];
        method = ClassUtils.getUserMethod(method);
        final Parameter[] parameters = method.getParameters();
        final String[] parameterNames = ParameterNameDiscoverer.getParameterNames(
            method
        );
        for (final ParameterInjector injector : this.parameterInjectors.values()) {
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

    protected Object processBeanBefore(final Binding binding, Object instance) {
        for (final BeanBeforeProcessor processor : this.beanBeforeProcessors.values()) {
            instance = processor.process(this, binding, instance);
        }
        return instance;
    }

    protected Object processBeanAfter(final Binding binding, Object instance) {
        for (final BeanAfterProcessor processor : this.beanAfterProcessors.values()) {
            instance = processor.process(this, binding, instance);
        }
        return instance;
    }

    protected boolean aspectMatches(final Class<?> type) {
        // Disable proxy Advice and AspectManager
        if (
            Advice.class.isAssignableFrom(type) || type == AspectManager.class
        ) {
            return false;
        }
        // Disable some bootstrap
        if (type == LoadEnvironmentVariables.class) {
            return false;
        }
        if (ClassUtils.isSkipBuildType(type)) {
            return false;
        }
        final AspectManager aspectManager = this.make(AspectManager.class);
        if (aspectManager == null) {
            return false;
        }
        return aspectManager.matches(type);
    }

    /* ===================== doBind ===================== */

    private synchronized Binding doBind(
        final String bindName,
        final Binding binding,
        final String alias
    ) {
        log.debug(
            "Container bind: {} - {}({})",
            binding.getScope(),
            bindName,
            alias
        );
        if (alias != null) {
            this.alias(alias, bindName);
        }
        return this.setBinding(bindName, binding);
    }

    protected synchronized Binding doBind(
        final String bindName,
        final Wrapper wrapper,
        final String alias,
        final ScopeType scopeType,
        final boolean overwrite
    ) {
        this.checkHasBinding(bindName, overwrite);
        final Binding binding = this.newBinding(bindName, wrapper, scopeType);
        return this.doBind(bindName, binding, alias);
    }

    /* ===================== doInstance ===================== */

    protected synchronized Container doInstance(
        final String instanceName,
        final Object instance,
        final String alias,
        final ScopeType scopeType
    ) {
        Binding binding = this.getBinding(instanceName);
        if (binding != null) {
            throw new IllegalStateException(
                "Target [" + instanceName + "] has been bind"
            );
        }
        binding = this.newBinding(instanceName, instance, scopeType);
        this.doBind(instanceName, binding, alias);
        return this;
    }

    /* ===================== doBuild ===================== */

    protected synchronized Object doBuild(final Binding binding) {
        final Class<?> instanceType = binding.getInstanceType();
        if (instanceType == null) {
            return null;
        }
        // 排除 JDK 自带类的 doBuild
        if (ClassUtils.isSkipBuildType(instanceType)) {
            return ClassUtil.getDefaultValue(instanceType);
        }
        log.debug("Container build: {}", instanceType);
        final Constructor<?>[] constructors = ReflectUtils.sortConstructors(
            instanceType.getDeclaredConstructors()
        );
        Object instance;
        final List<Exception> errors = new ArrayList<>();
        for (final Constructor<?> constructor : constructors) {
            constructor.setAccessible(true);
            final Object[] dependencies =
                this.processParameterInjector(binding, constructor);
            try {
                instance = constructor.newInstance(dependencies);
            } catch (final Exception e) {
                errors.add(e);
                continue;
            }
            instance = this.processInstanceInjector(binding, instance);
            if (this.aspectMatches(instanceType)) {
                instance =
                    ProxyCreator.createAop(
                        this.make(AspectManager.class),
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
        log.error(
            "Build instance failed, use default value, Type: {}",
            instanceType
        );
        for (final Exception error : errors) {
            log.error("Build instance failed error", error);
        }
        return ClassUtil.getDefaultValue(instanceType);
    }

    /* ===================== doMake ===================== */

    protected synchronized <T> T doMake(
        final String instanceName,
        final Class<T> returnType
    ) {
        log.debug("Container make: {} - {}", instanceName, returnType);
        final Binding binding = this.getOrDefaultBinding(instanceName);
        final ScopeType scopeType = binding.getScope();
        Object instance = binding.getInstance();
        if (instance != null) {
            return Convert.convert(
                returnType,
                ReflectUtils.resolveAutowiringValue(instance, returnType)
            );
        }
        try {
            instance =
                binding.getWrapper().getInstance(this, this.dataBinder.get());
        } catch (final Throwable e) {
            throw new ContainerException("Instance make failed", e);
        }
        instance = ReflectUtils.resolveAutowiringValue(instance, returnType);
        final T returnInstance = Convert.convert(returnType, instance);
        if (scopeType.isShared()) {
            binding.setInstance(returnInstance);
        }
        return returnInstance;
    }

    /* ===================== doRemove ===================== */

    protected synchronized Container doRemove(final String name) {
        log.debug("Container remove: {}", name);
        final Binding binding = this.getBinding(name);
        if (binding.isCreated()) {
            this.processBeanAfter(binding, binding.getInstance());
            this.removeBinding(name);
        }
        return this;
    }

    /* ===================== callMethod =============== */

    protected <T> T callMethod(
        final Object instance,
        final Method method,
        final Class<T> returnType
    ) {
        log.debug("Container call method: {} - {}", method, returnType);
        final Object[] dependencies =
            this.processParameterInjector(null, method);
        return Convert.convert(
            returnType,
            ReflectUtil.invoke(instance, method, dependencies)
        );
    }

    protected <T> T callMethod(
        final Object instance,
        final String methodName,
        final Class<T> returnType
    ) {
        final Method[] methods = Arrays
            .stream(instance.getClass().getMethods())
            .filter(m -> m.getName().equals(methodName))
            .toArray(Method[]::new);
        if (methods.length == 0) {
            throw new NullPointerException(
                "The specified method was not found"
            );
        } else if (methods.length > 1) {
            throw new IllegalCallerException(
                "The called method cannot be overloaded"
            );
        }
        return this.callMethod(instance, methods[0], returnType);
    }

    protected <T> T callMethod(
        final String typeName,
        final String methodName,
        final Class<T> returnType
    ) {
        return this.callMethod(this.make(typeName), methodName, returnType);
    }

    protected <T> T callMethod(
        final Class<?> type,
        final String methodName,
        final Class<T> returnType
    ) {
        return this.callMethod(type.getName(), methodName, returnType);
    }

    protected <T> T callMethod(
        final String typeName,
        final String methodName,
        final Class<?>[] paramTypes,
        final Class<T> returnType
    ) {
        final Object instance = this.make(typeName);
        return this.callMethod(instance, methodName, returnType);
    }

    /*======================  alias  ==================*/

    public void alias(final String alias, final String name) {
        this.setAlias(alias, name);
    }

    public void alias(final String alias, final Class<?> type) {
        this.alias(alias, type.getName());
    }

    /* ===================== build ==================== */

    public Object build(final String instanceName) {
        return this.doBuild(this.getOrDefaultBinding(instanceName));
    }

    public Object build(final Class<?> instanceType) {
        return this.doBuild(this.getOrDefaultBinding(instanceType));
    }

    public Object build(final Binding binding) {
        return this.doBuild(binding);
    }

    /* ===================== bind ===================== */

    // String
    // String, Wrapper

    public Binding bind(final String bindName) {
        return this.bind(
                bindName,
                (container, with) -> container.build(bindName)
            );
    }

    public Binding bind(final String bindName, final Wrapper wrapper) {
        return this.bind(bindName, wrapper, null);
    }

    public Binding bind(
        final String bindName,
        final Wrapper wrapper,
        final String alias
    ) {
        return this.bind(bindName, wrapper, alias, ScopeType.PROTOTYPE);
    }

    public Binding bind(
        final String bindName,
        final Wrapper wrapper,
        final String alias,
        final ScopeType scopeType
    ) {
        return this.bind(bindName, wrapper, alias, scopeType, false);
    }

    public Binding bind(
        final String bindName,
        final Wrapper wrapper,
        final String alias,
        final ScopeType scopeType,
        final boolean overwrite
    ) {
        return this.doBind(bindName, wrapper, alias, scopeType, overwrite);
    }

    // Class
    // Class, Wrapper

    public Binding bind(final Class<?> bingType) {
        return this.bind(
                bingType,
                (container, with) -> container.build(bingType)
            );
    }

    public Binding bind(final Class<?> bingType, final Wrapper wrapper) {
        return this.bind(bingType, wrapper, null);
    }

    public Binding bind(
        final Class<?> bingType,
        final Wrapper wrapper,
        final String alias
    ) {
        return this.bind(bingType, wrapper, alias, ScopeType.PROTOTYPE);
    }

    public Binding bind(
        final Class<?> bingType,
        final Wrapper wrapper,
        final String alias,
        final ScopeType scopeType
    ) {
        return this.bind(bingType, wrapper, alias, scopeType, false);
    }

    public Binding bind(
        final Class<?> bindType,
        final Wrapper wrapper,
        final String alias,
        final ScopeType scopeType,
        final boolean overwrite
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

    public Binding bind(final String bindName, final String wrapper) {
        return this.bind(bindName, wrapper, null);
    }

    public Binding bind(
        final String bindName,
        final String wrapper,
        final String alias
    ) {
        return this.bind(bindName, wrapper, alias, ScopeType.PROTOTYPE);
    }

    public Binding bind(
        final String bindName,
        final String wrapper,
        final String alias,
        final ScopeType scopeType
    ) {
        return this.bind(bindName, wrapper, alias, scopeType, false);
    }

    public Binding bind(
        final String bindName,
        final String wrapper,
        final String alias,
        final ScopeType scopeType,
        final boolean overwrite
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

    public Binding bind(final Class<?> bindType, final Class<?> wrapper) {
        return this.bind(bindType, wrapper, null);
    }

    public Binding bind(
        final Class<?> bindType,
        final Class<?> wrapper,
        final String alias
    ) {
        return this.bind(bindType, wrapper, alias, ScopeType.PROTOTYPE);
    }

    public Binding bind(
        final Class<?> bindType,
        final Class<?> wrapper,
        final String alias,
        final ScopeType scopeType
    ) {
        return this.bind(bindType, wrapper, alias, scopeType, false);
    }

    public Binding bind(
        final Class<?> bindType,
        final Class<?> wrapper,
        final String alias,
        final ScopeType scopeType,
        final boolean overwrite
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

    public Binding singleton(final String bindName) {
        return this.singleton(bindName, bindName);
    }

    public Binding singleton(final String bindName, final String wrapper) {
        return this.singleton(bindName, wrapper, null);
    }

    public Binding singleton(
        final String bindName,
        final String wrapper,
        final String alias
    ) {
        return this.singleton(bindName, wrapper, alias, false);
    }

    public Binding singleton(
        final String bindName,
        final String wrapper,
        final String alias,
        final boolean overwrite
    ) {
        return this.bind(
                bindName,
                wrapper,
                alias,
                ScopeType.SINGLETON,
                overwrite
            );
    }

    public Binding singleton(final String bindName, final Wrapper wrapper) {
        return this.singleton(bindName, wrapper, null);
    }

    public Binding singleton(
        final String bindName,
        final Wrapper wrapper,
        final String alias
    ) {
        return this.singleton(bindName, wrapper, alias, false);
    }

    public Binding singleton(
        final String bindName,
        final Wrapper wrapper,
        final String alias,
        final boolean overwrite
    ) {
        return this.bind(
                bindName,
                wrapper,
                alias,
                ScopeType.SINGLETON,
                overwrite
            );
    }

    public Binding singleton(final Class<?> bindType) {
        return this.singleton(bindType, bindType);
    }

    public Binding singleton(final Class<?> bindType, final Class<?> wrapper) {
        return this.singleton(bindType, wrapper, null);
    }

    public Binding singleton(
        final Class<?> bindType,
        final Class<?> wrapper,
        final String alias
    ) {
        return this.singleton(bindType, wrapper, alias, false);
    }

    public Binding singleton(
        final Class<?> bindType,
        final Class<?> wrapper,
        final String alias,
        final boolean overwrite
    ) {
        return this.bind(
                bindType,
                wrapper,
                alias,
                ScopeType.SINGLETON,
                overwrite
            );
    }

    public Binding singleton(final Class<?> bindType, final Wrapper wrapper) {
        return this.singleton(bindType, wrapper, null);
    }

    public Binding singleton(
        final Class<?> bindType,
        final Wrapper wrapper,
        final String alias
    ) {
        return this.singleton(bindType, wrapper, alias, false);
    }

    public Binding singleton(
        final Class<?> bindType,
        final Wrapper wrapper,
        final String alias,
        final boolean overwrite
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

    public Container instance(final String bindName, final Object instance) {
        return this.instance(bindName, instance, null, ScopeType.SINGLETON);
    }

    public Container instance(
        final String bindName,
        final Object instance,
        final ScopeType scopeType
    ) {
        return this.instance(bindName, instance, null, scopeType);
    }

    public Container instance(
        final String bindName,
        final Object instance,
        final String alias
    ) {
        return this.doInstance(bindName, instance, alias, ScopeType.SINGLETON);
    }

    public Container instance(
        final String bindName,
        final Object instance,
        final String alias,
        final ScopeType scopeType
    ) {
        return this.doInstance(bindName, instance, alias, scopeType);
    }

    public Container instance(final Class<?> bindType, final Object instance) {
        return this.instance(
                bindType.getName(),
                instance,
                null,
                ScopeType.SINGLETON
            );
    }

    public Container instance(
        final Class<?> bindType,
        final Object instance,
        final ScopeType scopeType
    ) {
        return this.instance(bindType.getName(), instance, null, scopeType);
    }

    public Container instance(
        final Class<?> bindType,
        final Object instance,
        final String alias
    ) {
        return this.instance(
                bindType.getName(),
                instance,
                alias,
                ScopeType.SINGLETON
            );
    }

    public Container instance(
        final Class<?> bindType,
        final Object instance,
        final String alias,
        final ScopeType scopeType
    ) {
        return this.instance(bindType.getName(), instance, alias, scopeType);
    }

    /* ======================= make =========================== */

    public Object make(final String bindName) {
        return this.make(bindName, Object.class);
    }

    public <T> T make(final String bindName, final Class<T> returnType) {
        return this.make(bindName, returnType, this.dataBinder.get());
    }

    public <T> T make(
        final String bindName,
        final Class<T> returnType,
        final DataBinder dataBinder
    ) {
        return this.withAndReset(
                () -> this.doMake(bindName, returnType),
                dataBinder
            );
    }

    public <T> T make(
        final String bindName,
        final Class<T> returnType,
        final Map<String, Object> args
    ) {
        return this.make(
                bindName,
                returnType,
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T make(final Class<T> bindType) {
        return this.make(bindType.getName(), bindType, this.dataBinder.get());
    }

    public <T> T make(final Class<T> bindType, final Map<String, Object> args) {
        return this.make(bindType.getName(), bindType, args);
    }

    public <T> T make(final Class<T> bindType, final DataBinder dataBinder) {
        return this.make(bindType.getName(), bindType, dataBinder);
    }

    /* ====================== remove ======================= */

    public Container remove(final String name) {
        return this.doRemove(name);
    }

    public Container remove(final Class<?> type) {
        return this.doRemove(type.getName());
    }

    /* ====================== call ========================= */

    public <T> T call(final String[] target, final Class<T> returnType) {
        if (target.length != ARRAY_METHOD_DEF_LENGTH) {
            throw new IllegalArgumentException(
                "The length of the target array must be 2"
            );
        }
        return this.callMethod(target[0], target[1], returnType);
    }

    public <T> T call(
        final String[] target,
        final Class<T> returnType,
        final Map<String, Object> args
    ) {
        return this.withAndReset(
                () -> this.call(target, returnType),
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T call(
        final String[] target,
        final Class<?>[] paramTypes,
        final Class<T> returnType
    ) {
        if (target.length != ARRAY_METHOD_DEF_LENGTH) {
            throw new IllegalArgumentException(
                "The length of the target array must be 2"
            );
        }
        return this.callMethod(target[0], target[1], paramTypes, returnType);
    }

    public <T> T call(
        final String[] target,
        final Class<?>[] paramTypes,
        final Class<T> returnType,
        final Map<String, Object> args
    ) {
        return this.withAndReset(
                () -> this.call(target, paramTypes, returnType),
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T call(final String target, final Class<T> returnType) {
        return this.call(target.split("@"), returnType);
    }

    public <T> T call(
        final String target,
        final Class<T> returnType,
        final Map<String, Object> args
    ) {
        return this.call(target.split("@"), returnType, args);
    }

    public <T> T call(
        final String target,
        final Class<?>[] paramTypes,
        final Class<T> returnType
    ) {
        return this.call(target.split("@"), paramTypes, returnType);
    }

    public <T> T call(
        final String target,
        final Class<?>[] paramTypes,
        final Class<T> returnType,
        final Map<String, Object> args
    ) {
        return this.call(target.split("@"), paramTypes, returnType, args);
    }

    public <T> T call(
        final Class<?> type,
        final Method method,
        final Class<T> returnType
    ) {
        return this.callMethod(this.make(type), method, returnType);
    }

    public <T> T call(
        final Class<?> type,
        final Method method,
        final Class<T> returnType,
        final Map<String, Object> args
    ) {
        return this.call(this.make(type), method, returnType, args);
    }

    public <T> T call(final Method method, final Class<T> returnType) {
        return this.call(method.getDeclaringClass(), method, returnType);
    }

    public <T> T call(
        final Method method,
        final Class<T> returnType,
        final Map<String, Object> args
    ) {
        return this.call(method.getDeclaringClass(), method, returnType, args);
    }

    public <T> T call(
        final Object instance,
        final Method method,
        final Class<T> returnType
    ) {
        return this.callMethod(instance, method, returnType);
    }

    public <T> T call(
        final Object instance,
        final Method method,
        final Class<T> returnType,
        final Map<String, Object> args
    ) {
        return this.call(
                instance,
                method,
                returnType,
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T call(
        final Object instance,
        final Method method,
        final Class<T> returnType,
        final DataBinder binder
    ) {
        return this.withAndReset(
                () -> this.callMethod(instance, method, returnType),
                binder
            );
    }

    public <T> T call(
        final Object instance,
        final String methodName,
        final Class<T> returnType
    ) {
        return this.callMethod(instance, methodName, returnType);
    }

    public <T> T call(
        final Object instance,
        final String methodName,
        final Class<T> returnType,
        final Map<String, Object> args
    ) {
        return this.call(
                instance,
                methodName,
                returnType,
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T call(
        final Object instance,
        final String methodName,
        final Class<T> returnType,
        final DataBinder dataBinder
    ) {
        return this.withAndReset(
                () -> this.callMethod(instance, methodName, returnType),
                dataBinder
            );
    }

    public <T> T call(
        final Class<?> type,
        final String methodName,
        final Class<T> returnType
    ) {
        return this.callMethod(type, methodName, returnType);
    }

    public <T> T call(
        final Class<?> type,
        final String methodName,
        final Class<T> returnType,
        final Map<String, Object> args
    ) {
        return this.call(
                type,
                methodName,
                returnType,
                new DefaultDataBinder(this, args)
            );
    }

    public <T> T call(
        final Class<?> type,
        final String methodName,
        final Class<T> returnType,
        final DataBinder binder
    ) {
        return this.withAndReset(
                () -> this.call(type, methodName, returnType),
                binder
            );
    }

    @SuppressWarnings("unchecked")
    public <T> T call(final Method method) {
        return (T) this.call(
                method.getDeclaringClass(),
                method,
                method.getReturnType()
            );
    }

    public <T> T call(final Method method, final DataBinder binder) {
        return this.withAndReset(() -> this.call(method), binder);
    }

    /* ===================================================== */

    public DataBinder getDataBinder() {
        return this.dataBinder.get();
    }

    public Container with(final Map<String, Object> args) {
        return this.with(null, args);
    }

    public Container with(final String prefix, final Map<String, Object> args) {
        this.dataBinder.set(new DefaultDataBinder(this, args));
        return this;
    }

    public Container resetWith() {
        this.dataBinder.set(
                new DefaultDataBinder(this, new ConcurrentHashMap<>(256))
            );
        return this;
    }

    public <T> T withAndReset(
        final Supplier<T> callback,
        final DataBinder dataBinder
    ) {
        final DataBinder reset = this.dataBinder.get();
        this.dataBinder.set(dataBinder);
        final T result = callback.get();
        this.dataBinder.set(reset);
        return result;
    }

    public Map<Class<? extends Context>, Context> getContexts() {
        return contexts;
    }

    public Container addInstanceInjector(final InstanceInjector injector) {
        log.debug("Container add instance injector: {}", injector);
        this.instanceInjectors.put(injector.getClass(), injector);
        return this;
    }

    public Container removeInstanceInjector(
        final Class<? extends InstanceInjector> injector
    ) {
        log.debug("Container remove instance injector: {}", injector);
        this.instanceInjectors.remove(injector);
        return this;
    }

    public Map<Class<? extends InstanceInjector>, InstanceInjector> getInstanceInjectors() {
        return instanceInjectors;
    }

    public Container addParameterInjector(final ParameterInjector injector) {
        log.debug("Container add parameter injector: {}", injector);
        this.parameterInjectors.put(injector.getClass(), injector);
        return this;
    }

    public Container removeParameterInjector(
        final Class<? extends ParameterInjector> injector
    ) {
        log.debug("Container remove parameter injector: {}", injector);
        this.parameterInjectors.remove(injector);
        return this;
    }

    public Map<Class<? extends ParameterInjector>, ParameterInjector> getParameterInjectors() {
        return parameterInjectors;
    }

    public Container addBeanBeforeProcessor(
        final BeanBeforeProcessor processor
    ) {
        log.debug("Container add bean before processor: {}", processor);
        this.beanBeforeProcessors.put(processor.getClass(), processor);
        return this;
    }

    public Container removeBeanBeforeProcessor(
        final Class<? extends BeanBeforeProcessor> processor
    ) {
        log.debug("Container remove bean before processor: {}", processor);
        this.beanBeforeProcessors.remove(processor);
        return this;
    }

    public Map<Class<? extends BeanBeforeProcessor>, BeanBeforeProcessor> getBeanBeforeProcessors() {
        return beanBeforeProcessors;
    }

    public Container addBeanAfterProcessor(final BeanAfterProcessor processor) {
        log.debug("Container add bean after processor: {}", processor);
        this.beanAfterProcessors.put(processor.getClass(), processor);
        return this;
    }

    public Container removeBeanAfterProcessor(
        final Class<? extends BeanAfterProcessor> processor
    ) {
        log.debug("Container remove bean after processor: {}", processor);
        this.beanAfterProcessors.remove(processor);
        return this;
    }

    public Map<Class<? extends BeanAfterProcessor>, BeanAfterProcessor> getBeanAfterProcessors() {
        return beanAfterProcessors;
    }
}
