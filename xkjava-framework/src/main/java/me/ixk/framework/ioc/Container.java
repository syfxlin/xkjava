/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import me.ixk.framework.exception.ContainerException;
import me.ixk.framework.ioc.binder.DataBinder;
import me.ixk.framework.ioc.binder.DefaultDataBinder;
import me.ixk.framework.ioc.context.Context;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.ioc.entity.Binding;
import me.ixk.framework.ioc.entity.ConstructorContext;
import me.ixk.framework.ioc.entity.InjectContext;
import me.ixk.framework.ioc.entity.ParameterContext;
import me.ixk.framework.ioc.factory.FactoryBean;
import me.ixk.framework.ioc.factory.ObjectFactory;
import me.ixk.framework.ioc.factory.ObjectProvider;
import me.ixk.framework.ioc.injector.InstanceInjector;
import me.ixk.framework.ioc.injector.ParameterInjector;
import me.ixk.framework.ioc.processor.BeanAfterCreateProcessor;
import me.ixk.framework.ioc.processor.BeanDestroyProcessor;
import me.ixk.framework.ioc.processor.BeforeInjectProcessor;
import me.ixk.framework.ioc.type.TypeWrapper;
import me.ixk.framework.util.ClassUtils;
import me.ixk.framework.util.Convert;
import me.ixk.framework.util.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 容器
 *
 * @author Otstar Lin
 * @date 2020/12/23 下午 10:37
 */
public class Container {

    private static final Logger log = LoggerFactory.getLogger(Container.class);

    private static final String FACTORY_BEAN_PREFIX = "&_";
    private static final String ATTRIBUTE_PREFIX = "$_";
    private static final DataBinder DEFAULT_DATA_BINDER = new DefaultDataBinder(
        Collections.emptyMap()
    );

    /**
     * 参数注入器
     */
    private final Deque<ParameterInjector> parameterInjectors = new ConcurrentLinkedDeque<>();

    /**
     * 实例注入器
     */
    private final Deque<InstanceInjector> instanceInjectors = new ConcurrentLinkedDeque<>();

    /**
     * 创建前置处理器，在初始化前进行
     */
    private final Deque<BeforeInjectProcessor> beforeInjectProcessors = new ConcurrentLinkedDeque<>();
    /**
     * 创建后置处理器，在初始化后进行
     */
    private final Deque<BeanAfterCreateProcessor> beanAfterCreateProcessors = new ConcurrentLinkedDeque<>();
    /**
     * 销毁处理器，在删除前进行
     */
    private final Deque<BeanDestroyProcessor> beanDestroyProcessors = new ConcurrentLinkedDeque<>();

    /**
     * Contexts，存储实例的空间
     */
    private final Map<String, Context> contexts = new LinkedHashMap<>(5);

    /**
     * Bindings key：Bean 名称
     * <p>
     * bindings，bindingNamesByType，aliases 均使用 bindings 的对象锁
     */
    private final ConcurrentMap<String, Binding> bindings = new ConcurrentHashMap<>(
        256
    );

    /**
     * Bindings 类型索引 key：类型，value：Bean 名称
     */
    private final ConcurrentMap<Class<?>, List<String>> bindingNamesByType = new ConcurrentHashMap<>(
        265
    );

    /**
     * 别名
     */
    private final ConcurrentMap<String, String> aliases = new ConcurrentHashMap<>(
        256
    );

    private final TransmittableThreadLocal<Map<String, Object>> earlyBeans = new TransmittableThreadLocal<>();

    public Container() {
        log.info("Container created");
    }

    /**
     * 销毁方法
     */
    public void destroy() {
        synchronized (this.contexts) {
            for (final String scopeType : new ArrayList<>(
                this.contexts.keySet()
            )) {
                this.removeContext(scopeType);
            }
            log.info("Container destroyed");
        }
    }

    public ConcurrentMap<String, Binding> getBindings() {
        return bindings;
    }

    public ConcurrentMap<Class<?>, List<String>> getBindingNamesByType() {
        return bindingNamesByType;
    }

    public ConcurrentMap<String, String> getAliases() {
        return aliases;
    }

    /* ===================== Context ===================== */

    public void registerContext(final String scopeType, final Context context) {
        if (log.isDebugEnabled()) {
            log.debug("Container registered context: {}", scopeType);
        }
        synchronized (this.contexts) {
            this.contexts.put(scopeType, context);
        }
    }

    public void removeContext(final String scopeType) {
        synchronized (this.contexts) {
            if (log.isDebugEnabled()) {
                log.debug("Container remove context: {}", scopeType);
            }
            for (final Entry<String, Binding> entry : this.bindings.entrySet()) {
                if (entry.getValue().getScope().equals(scopeType)) {
                    this.doRemove(entry.getKey(), DEFAULT_DATA_BINDER);
                }
            }
            this.contexts.remove(scopeType);
            for (Entry<String, String> entry : this.aliases.entrySet()) {
                if (!this.bindings.containsKey(entry.getValue())) {
                    this.aliases.remove(entry.getKey());
                }
            }
        }
    }

    public Context getContextByScope(final String scopeType) {
        synchronized (this.contexts) {
            return this.contexts.get(scopeType);
        }
    }

    /* ===================== Binding ===================== */

    protected Binding newBinding(
        final String name,
        final Class<?> instanceType,
        final String scopeType
    ) {
        return new Binding(
            this.getContextByScope(scopeType),
            name,
            instanceType,
            scopeType
        );
    }

    protected Binding newBinding(
        final String name,
        final Object instance,
        final String scopeType
    ) {
        return new Binding(
            this.getContextByScope(scopeType),
            name,
            instance,
            scopeType
        );
    }

    protected Binding newBinding(
        final String name,
        final FactoryBean<?> factoryBean,
        final String scopeType
    ) {
        return new Binding(
            this.getContextByScope(scopeType),
            name,
            factoryBean,
            scopeType
        );
    }

    public boolean has(final String name) {
        return this.getBinding(name) != null;
    }

    public boolean has(final Class<?> type) {
        return this.has(this.getBeanNameByType(type));
    }

    public Binding getBinding(final Class<?> type) {
        return this.getBinding(this.getBeanNameByType(type));
    }

    public Binding getBinding(final String name) {
        return this.bindings.get(this.getCanonicalName(name));
    }

    public void setBinding(String name, final Binding binding) {
        if (log.isDebugEnabled()) {
            log.debug("Container set binding: {}", name);
        }
        synchronized (this.bindings) {
            name = this.getCanonicalName(name);
            this.bindings.put(name, binding);
            Class<?> clazz = binding.getType();
            while (clazz != null && !ClassUtils.isSkipBuildType(clazz)) {
                this.addType(name, clazz, binding.isPrimary());
                for (final Class<?> in : clazz.getInterfaces()) {
                    this.addType(name, in, binding.isPrimary());
                }
                clazz = clazz.getSuperclass();
            }
        }
    }

    protected void validHas(final String name, final String message) {
        synchronized (this.bindings) {
            if (this.has(name)) {
                throw new IllegalStateException(String.format(message, name));
            }
        }
    }

    public void removeBinding(final String name) {
        if (log.isDebugEnabled()) {
            log.debug("Container remove binding: {}", name);
        }
        synchronized (this.bindings) {
            String realName = this.getCanonicalName(name);
            final Binding binding = this.bindings.get(realName);
            if (binding == null) {
                return;
            }
            Class<?> clazz = binding.getType();
            while (clazz != null && !ClassUtils.isSkipBuildType(clazz)) {
                this.removeType(realName, clazz);
                for (final Class<?> in : clazz.getInterfaces()) {
                    this.removeType(realName, in);
                }
                clazz = clazz.getSuperclass();
            }
            this.removeAlias(name);
            this.bindings.remove(realName);
        }
    }

    private void addType(
        final String name,
        final Class<?> type,
        final boolean isPrimary
    ) {
        this.bindingNamesByType.compute(
                type,
                (t, o) -> {
                    if (o != null) {
                        if (isPrimary) {
                            o.add(0, name);
                        } else {
                            o.add(name);
                        }
                        return o;
                    } else {
                        final List<String> list = new CopyOnWriteArrayList<>();
                        list.add(name);
                        return list;
                    }
                }
            );
    }

    private void removeType(final String name, final Class<?> type) {
        this.bindingNamesByType.computeIfPresent(
                type,
                (k, v) -> {
                    v.remove(name);
                    return v;
                }
            );
    }

    public String getBeanNameByType(final Class<?> type) {
        final List<String> list = this.bindingNamesByType.get(type);
        if (list == null || list.isEmpty()) {
            // 未找到或空则使用短类名作为名称
            return this.typeToBeanName(type);
        }
        // 否则取第一个返回
        return list.get(0);
    }

    public String typeToBeanName(final Class<?> type) {
        final String name = type.getSimpleName();
        if (name.length() == 0) {
            return name;
        }
        if (
            name.length() > 1 &&
            Character.isUpperCase(name.charAt(1)) &&
            Character.isUpperCase(name.charAt(0))
        ) {
            return name;
        }
        final char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /* ======================= Bean ======================= */

    public List<String> getBeanNamesForType(final Class<?> type) {
        return this.bindingNamesByType.get(type);
    }

    public <T> Map<String, T> getBeanOfType(final Class<T> type) {
        return this.getBeanOfType(type, DEFAULT_DATA_BINDER);
    }

    public <T> Map<String, T> getBeanOfType(
        final Class<T> type,
        final DataBinder dataBinder
    ) {
        final List<String> list = this.getBeanNamesForType(type);
        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }
        return list
            .stream()
            .collect(
                Collectors.toMap(
                    name -> name,
                    name -> this.make(name, type, dataBinder)
                )
            );
    }

    public List<String> getBeanNamesForAnnotation(
        final Class<? extends Annotation> annotationType
    ) {
        final List<String> list = new ArrayList<>();
        for (final Entry<String, Binding> entry : this.bindings.entrySet()) {
            final Class<?> type = entry.getValue().getType();
            if (
                type.isInterface() ||
                type.isEnum() ||
                type.isAnnotation() ||
                type.isPrimitive() ||
                type.isArray() ||
                entry.getValue().getAnnotation().notAnnotation(annotationType)
            ) {
                continue;
            }
            list.add(entry.getKey());
        }
        return list;
    }

    public Map<String, Object> getBeansWithAnnotation(
        final Class<? extends Annotation> annotationType
    ) {
        return this.getBeansWithAnnotation(annotationType, DEFAULT_DATA_BINDER);
    }

    public Map<String, Object> getBeansWithAnnotation(
        final Class<? extends Annotation> annotationType,
        final DataBinder dataBinder
    ) {
        final List<String> list =
            this.getBeanNamesForAnnotation(annotationType);
        if (list.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, Object> beans = new HashMap<>(list.size());
        for (final String name : list) {
            beans.put(name, this.make(name, Object.class, dataBinder));
        }
        return beans;
    }

    /* ===================== Alias ===================== */

    private String getCanonicalName(final String name) {
        final String resolve = this.getAlias(name);
        if (resolve == null) {
            return name;
        }
        return resolve;
    }

    public void setAlias(final String alias, final String name) {
        if (alias == null || alias.equals(name)) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Container add alias: {} => {}", alias, name);
        }
        // 二元操作，由于验证包含 alias 和 bindings，并发工具只能保护一个，所以要加锁
        synchronized (this.bindings) {
            this.validHas(alias, "Alias [%s] has contains");
            this.aliases.put(alias, name);
        }
    }

    public void removeAlias(final String alias) {
        if (log.isDebugEnabled()) {
            log.debug("Container remove alias: {}", alias);
        }
        synchronized (this.bindings) {
            this.aliases.remove(alias);
        }
    }

    public boolean hasAlias(final String alias) {
        return this.getAlias(alias) != null;
    }

    public String getAlias(final String alias) {
        return this.aliases.get(alias);
    }

    /*======================  Attribute  ==================*/

    public void removeAttribute(final String name) {
        synchronized (this.contexts) {
            for (final Context context : this.contexts.values()) {
                if (context.has(ATTRIBUTE_PREFIX + name)) {
                    context.remove(ATTRIBUTE_PREFIX + name);
                    return;
                }
            }
        }
    }

    public Object getAttribute(final String name) {
        synchronized (this.contexts) {
            for (final Context context : this.contexts.values()) {
                if (context.has(ATTRIBUTE_PREFIX + name)) {
                    return context.get(ATTRIBUTE_PREFIX + name);
                }
            }
            return null;
        }
    }

    public boolean hasAttribute(final String name) {
        return this.getAttribute(name) != null;
    }

    public void setAttribute(
        final String name,
        final Object attribute,
        final String scopeType
    ) {
        this.getContextByScope(scopeType)
            .set(ATTRIBUTE_PREFIX + name, attribute);
    }

    public void removeAttribute(final String name, final String scopeType) {
        this.getContextByScope(scopeType).remove(ATTRIBUTE_PREFIX + name);
    }

    public Object getAttribute(final String name, final String scopeType) {
        return this.getContextByScope(scopeType).get(ATTRIBUTE_PREFIX + name);
    }

    public boolean hasAttribute(final String name, final String scopeType) {
        return this.getAttribute(name, scopeType) != null;
    }

    /* ===================== Process ===================== */

    protected InjectContext processBeforeInject(
        final Binding binding,
        final DataBinder dataBinder
    ) {
        final InjectContext context = new InjectContext(binding, dataBinder);
        for (final BeforeInjectProcessor processor : this.beforeInjectProcessors) {
            processor.process(this, context);
        }
        return context;
    }

    protected Object processInstanceInjector(
        final InjectContext context,
        Object instance
    ) {
        for (final InstanceInjector injector : this.instanceInjectors) {
            instance = injector.process(this, instance, context);
        }
        return instance;
    }

    protected Object[] processParameterInjector(
        final InjectContext context,
        Executable method
    ) {
        Object[] dependencies = new Object[method.getParameterCount()];
        method = ClassUtils.getUserMethod(method);
        final ParameterContext parameterContext = new ParameterContext(
            context,
            method
        );
        for (final ParameterInjector injector : this.parameterInjectors) {
            dependencies =
                injector.process(this, dependencies, parameterContext);
        }
        return dependencies;
    }

    protected Object processBeanAfterCreate(
        final InjectContext context,
        Object instance,
        final Constructor<?> constructor,
        final Object[] args
    ) {
        final ConstructorContext constructorContext = new ConstructorContext(
            constructor,
            args
        );
        for (final BeanAfterCreateProcessor processor : this.beanAfterCreateProcessors) {
            instance =
                processor.process(this, instance, context, constructorContext);
        }
        return instance;
    }

    protected void processBeanDestroy(
        final Binding binding,
        final Object instance,
        final DataBinder dataBinder
    ) {
        final InjectContext context = new InjectContext(binding, dataBinder);
        for (final BeanDestroyProcessor processor : this.beanDestroyProcessors) {
            processor.process(this, instance, context);
        }
    }

    /* ===================== doBind ===================== */

    protected Binding doBind(final String name, final Binding binding) {
        if (log.isDebugEnabled()) {
            log.debug("Container bind: {} - {}", binding.getScope(), name);
        }
        this.setBinding(name, binding);
        return binding;
    }

    protected Binding doBind(
        final String name,
        final FactoryBean<?> factoryBean,
        final String scopeType
    ) {
        synchronized (this.bindings) {
            this.validHas(name, "Target [%s] has been bind");
            final Binding binding =
                this.newBinding(name, factoryBean, scopeType);
            return this.doBind(name, binding);
        }
    }

    protected Binding doBind(
        final String name,
        final Class<?> instanceType,
        final String scopeType
    ) {
        synchronized (this.bindings) {
            this.validHas(name, "Target [%s] has been bind");
            final Binding binding =
                this.newBinding(name, instanceType, scopeType);
            return this.doBind(name, binding);
        }
    }

    protected Binding doBind(
        final String name,
        final Object instance,
        final String scopeType
    ) {
        synchronized (this.bindings) {
            this.validHas(name, "Target [%s] has been bind");
            final Binding binding = this.newBinding(name, instance, scopeType);
            return this.doBind(name, binding);
        }
    }

    /* ===================== doBuild ===================== */

    protected Object doBuild(
        final Binding binding,
        final DataBinder dataBinder
    ) {
        final Class<?> instanceType = binding.getType();
        if (instanceType == null) {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Container build: {}", instanceType);
        }
        Map<String, Object> earlyBeans = this.earlyBeans.get();
        if (earlyBeans != null && earlyBeans.containsKey(binding.getName())) {
            return earlyBeans.get(binding.getName());
        }
        final Constructor<?>[] constructors = ReflectUtils.sortConstructors(
            instanceType.getDeclaredConstructors()
        );
        Object instance;
        final List<Exception> errors = new ArrayList<>();
        for (final Constructor<?> constructor : constructors) {
            constructor.setAccessible(true);
            final InjectContext context =
                this.processBeforeInject(binding, dataBinder);
            final Object[] dependencies =
                this.processParameterInjector(context, constructor);
            try {
                instance = constructor.newInstance(dependencies);
            } catch (final Exception e) {
                errors.add(e);
                continue;
            }
            boolean createEarlyMap = false;
            earlyBeans = this.earlyBeans.get();
            if (earlyBeans == null) {
                earlyBeans = new HashMap<>();
                earlyBeans.put(binding.getName(), instance);
                this.earlyBeans.set(earlyBeans);
                createEarlyMap = true;
            }
            try {
                instance = this.processInstanceInjector(context, instance);
                instance =
                    this.processBeanAfterCreate(
                            context,
                            instance,
                            constructor,
                            dependencies
                        );
            } finally {
                if (createEarlyMap) {
                    this.earlyBeans.remove();
                }
            }
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

    protected <T> T doResolveType(
        final String name,
        final Class<T> returnType,
        final TypeWrapper<T> typeWrapper,
        final DataBinder dataBinder
    ) {
        if (returnType.isArray()) {
            // 注入类型为数组
            return Convert.convert(
                returnType,
                this.getBeanOfType(returnType.getComponentType(), dataBinder)
                    .values()
            );
        } else if (
            Collection.class.isAssignableFrom(returnType) &&
            returnType.isInterface()
        ) {
            // 注入集合
            final Class<?> componentType = typeWrapper.getGeneric(0);
            if (componentType == null) {
                return null;
            }
            return Convert.convert(
                returnType,
                this.getBeanOfType(componentType, dataBinder).values()
            );
        } else if (Map.class == returnType) {
            final Class<?> keyType = typeWrapper.getGeneric(0);
            if (String.class != keyType) {
                return null;
            }
            final Class<?> valueType = typeWrapper.getGeneric(1);
            if (valueType == null) {
                return null;
            }
            return Convert.convert(
                returnType,
                this.getBeanOfType(valueType, dataBinder).values()
            );
        } else if (ObjectFactory.class == returnType) {
            final Class<?> componentType = typeWrapper.getGeneric(0);
            if (componentType == null) {
                return null;
            }
            return Convert.convert(
                returnType,
                (ObjectFactory<Object>) () ->
                    make(name, componentType, dataBinder)
            );
        } else if (ObjectProvider.class == returnType) {
            final Class<?> componentType = typeWrapper.getGeneric(0);
            if (componentType == null) {
                return null;
            }
            return Convert.convert(
                returnType,
                new ObjectProvider<>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public Collection<Object> getObjects() {
                        return (Collection<Object>) getBeanOfType(
                            componentType,
                            dataBinder
                        )
                            .values();
                    }

                    @Override
                    public Object getObject() {
                        return make(name, componentType, dataBinder);
                    }
                }
            );
        }
        return null;
    }

    protected <T> T doMake(
        final String name,
        final Class<T> returnType,
        final DataBinder dataBinder
    ) {
        return this.doMake(name, TypeWrapper.forClass(returnType), dataBinder);
    }

    @SuppressWarnings("unchecked")
    protected <T> T doMake(
        final String name,
        final TypeWrapper<T> typeWrapper,
        final DataBinder dataBinder
    ) {
        final Class<T> returnType = typeWrapper.getClazz();
        if (log.isDebugEnabled()) {
            log.debug("Container make: {} - {}", name, returnType);
        }
        Binding binding = name == null ? null : this.getBinding(name);
        if (binding == null) {
            final T resolved =
                this.doResolveType(name, returnType, typeWrapper, dataBinder);
            if (resolved != null) {
                return resolved;
            }
            if (ClassUtils.isSkipBuildType(returnType)) {
                return (T) ClassUtil.getDefaultValue(returnType);
            } else {
                binding = this.getBinding(this.getBeanNameByType(returnType));
            }
        }
        if (binding == null) {
            binding = this.newBinding(name, returnType, ScopeType.PROTOTYPE);
        }
        final boolean proxy = typeWrapper.useProxy();
        Object instance = binding.getSource(proxy);
        if (instance != null) {
            return Convert.convert(returnType, instance);
        }
        // 加锁，双重检查，防止二次初始化
        synchronized (binding.getMutex()) {
            instance = binding.getSource(proxy);
            if (instance != null) {
                return Convert.convert(returnType, instance);
            }
            final Binding finalBinding = binding;
            try {
                FactoryBean<?> factoryBean = binding.getFactoryBean();
                if (factoryBean == null) {
                    factoryBean =
                        new FactoryBean<>() {
                            @Override
                            public Object getObject() {
                                return doBuild(finalBinding, dataBinder);
                            }

                            @Override
                            public Class<?> getObjectType() {
                                return finalBinding.getType();
                            }
                        };
                }
                instance = factoryBean.getObject();
            } catch (final Throwable e) {
                throw new ContainerException("Instance make failed", e);
            }
            final T returnInstance = Convert.convert(returnType, instance);
            if (binding.isShared()) {
                binding.setSource(returnInstance);
            }
            return returnInstance;
        }
    }

    /* ===================== doRemove ===================== */

    protected void doRemove(final String name, final DataBinder dataBinder) {
        if (log.isDebugEnabled()) {
            log.debug("Container remove: {}", name);
        }
        synchronized (this.bindings) {
            final Binding binding = this.getBinding(name);
            if (binding.isCreated()) {
                this.processBeanDestroy(
                        binding,
                        binding.getSource(),
                        dataBinder
                    );
            }
            this.removeBinding(name);
        }
    }

    /* ===================== doCall =============== */

    protected <T> T doCall(
        final Object instance,
        final Method method,
        final Class<T> returnType,
        final DataBinder dataBinder
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container call method: {} - {}", method, returnType);
        }
        final String name = instance == null
            ? "null"
            : this.typeToBeanName(instance.getClass());
        final InjectContext context =
            this.processBeforeInject(
                    this.newBinding(name, instance, ScopeType.PROTOTYPE),
                    dataBinder
                );
        final Object[] dependencies =
            this.processParameterInjector(context, method);
        return Convert.convert(
            returnType,
            ReflectUtil.invoke(instance, method, dependencies)
        );
    }

    /* ===================== bind ===================== */

    // name, factory
    // name, type
    @SuppressWarnings("unchecked")
    public Binding bind(
        final String name,
        final Class<?> type,
        final String scopeType
    ) {
        if (FactoryBean.class.isAssignableFrom(type)) {
            return this.bind(
                    name,
                    this.make(
                            FACTORY_BEAN_PREFIX + name,
                            (Class<? extends FactoryBean<?>>) type
                        ),
                    scopeType
                );
        } else {
            return this.doBind(name, type, scopeType);
        }
    }

    public Binding bind(
        final String name,
        final FactoryBean<?> factoryBean,
        final String scopeType
    ) {
        return this.doBind(name, factoryBean, scopeType);
    }

    public Binding bind(final String name, final Class<?> type) {
        return this.bind(name, type, ScopeType.SINGLETON);
    }

    public Binding bind(final String name, final FactoryBean<?> factoryBean) {
        return this.bind(name, factoryBean, ScopeType.SINGLETON);
    }

    // name, instance

    public Binding instance(
        final String name,
        final Object instance,
        final String scopeType
    ) {
        return this.doBind(name, instance, scopeType);
    }

    public Binding instance(final String name, final Object instance) {
        return this.instance(name, instance, ScopeType.SINGLETON);
    }

    // factory
    // type
    @SuppressWarnings("unchecked")
    public Binding bind(final Class<?> type, final String scopeType) {
        if (FactoryBean.class.isAssignableFrom(type)) {
            final FactoryBean<?> factoryBean =
                this.make((Class<? extends FactoryBean<?>>) type);
            return this.bind(
                    this.typeToBeanName(factoryBean.getObjectType()),
                    factoryBean,
                    scopeType
                );
        } else {
            return this.bind(this.typeToBeanName(type), type, scopeType);
        }
    }

    public Binding bind(final Class<?> type) {
        return this.bind(type, ScopeType.SINGLETON);
    }

    public Binding bind(
        final FactoryBean<?> factoryBean,
        final String scopeType
    ) {
        return this.bind(
                this.typeToBeanName(factoryBean.getObjectType()),
                factoryBean,
                scopeType
            );
    }

    public Binding bind(final FactoryBean<?> factoryBean) {
        return this.bind(factoryBean, ScopeType.SINGLETON);
    }

    // instance

    public Binding instance(final Object instance, final String scopeType) {
        return this.instance(
                this.typeToBeanName(instance.getClass()),
                instance,
                scopeType
            );
    }

    public Binding instance(final Object instance) {
        return this.instance(instance, ScopeType.SINGLETON);
    }

    /* ===================== make ===================== */

    public <T> T make(final Class<T> returnType) {
        return this.make(returnType, DEFAULT_DATA_BINDER);
    }

    public <T> T make(final String name, final Class<T> returnType) {
        return this.make(name, returnType, DEFAULT_DATA_BINDER);
    }

    public <T> T make(final Class<T> returnType, final DataBinder dataBinder) {
        return this.make(
                this.getBeanNameByType(returnType),
                returnType,
                dataBinder
            );
    }

    public <T> T make(
        final String name,
        final Class<T> returnType,
        final DataBinder dataBinder
    ) {
        return this.doMake(name, returnType, dataBinder);
    }

    public <T> T make(final TypeWrapper<T> returnType) {
        return this.make(returnType, DEFAULT_DATA_BINDER);
    }

    public <T> T make(final String name, final TypeWrapper<T> returnType) {
        return this.make(name, returnType, DEFAULT_DATA_BINDER);
    }

    public <T> T make(
        final TypeWrapper<T> returnType,
        final DataBinder dataBinder
    ) {
        return this.doMake(
                this.getBeanNameByType(returnType.getClazz()),
                returnType,
                dataBinder
            );
    }

    public <T> T make(
        final String name,
        final TypeWrapper<T> returnType,
        final DataBinder dataBinder
    ) {
        return this.doMake(name, returnType, dataBinder);
    }

    /* ====================== remove ======================= */

    public void remove(final String name) {
        this.remove(name, DEFAULT_DATA_BINDER);
    }

    public void remove(final String name, final DataBinder dataBinder) {
        this.doRemove(name, dataBinder);
    }

    /* ====================== call ======================= */

    public <T> T call(final Object instance, final Method method) {
        return this.call(instance, method, DEFAULT_DATA_BINDER);
    }

    public <T> T call(
        final Object instance,
        final Method method,
        final Class<T> returnType
    ) {
        return this.call(instance, method, returnType, DEFAULT_DATA_BINDER);
    }

    public <T> T call(
        final Object instance,
        final String methodName,
        final Class<T> returnType
    ) {
        return this.call(instance, methodName, returnType, DEFAULT_DATA_BINDER);
    }

    public <T> T call(
        final String name,
        final String methodName,
        final Class<T> returnType
    ) {
        return this.call(name, methodName, returnType, DEFAULT_DATA_BINDER);
    }

    public <T> T call(
        final Class<?> type,
        final String methodName,
        final Class<T> returnType
    ) {
        return this.call(type, methodName, returnType, DEFAULT_DATA_BINDER);
    }

    public <T> T call(final Method method) {
        return this.call(method, DEFAULT_DATA_BINDER);
    }

    @SuppressWarnings("unchecked")
    public <T> T call(
        final Object instance,
        final Method method,
        final DataBinder dataBinder
    ) {
        return (T) this.call(
                instance,
                method,
                method.getReturnType(),
                dataBinder
            );
    }

    public <T> T call(
        final Object instance,
        final Method method,
        final Class<T> returnType,
        final DataBinder dataBinder
    ) {
        return this.doCall(instance, method, returnType, dataBinder);
    }

    public <T> T call(
        final Object instance,
        final String methodName,
        final Class<T> returnType,
        final DataBinder dataBinder
    ) {
        if (instance == null) {
            throw new NullPointerException("instance must not be null");
        }
        return this.call(
                instance,
                ReflectUtil.getMethodByName(instance.getClass(), methodName),
                returnType,
                dataBinder
            );
    }

    public <T> T call(
        final String name,
        final String methodName,
        final Class<T> returnType,
        final DataBinder dataBinder
    ) {
        return this.call(
                this.make(name, Object.class, dataBinder),
                methodName,
                returnType,
                dataBinder
            );
    }

    public <T> T call(
        final Class<?> type,
        final String methodName,
        final Class<T> returnType,
        final DataBinder dataBinder
    ) {
        return this.call(
                this.make(type, dataBinder),
                methodName,
                returnType,
                dataBinder
            );
    }

    public <T> T call(final Method method, final DataBinder dataBinder) {
        final Object instance = Modifier.isStatic(method.getModifiers())
            ? null
            : this.make(method.getDeclaringClass());
        return this.call(instance, method, dataBinder);
    }

    /* ===================================================== */

    public Container addFirstInstanceInjector(final InstanceInjector injector) {
        if (log.isDebugEnabled()) {
            log.debug("Container add instance injector: {}", injector);
        }
        this.instanceInjectors.addFirst(injector);
        return this;
    }

    public Container addInstanceInjector(final InstanceInjector injector) {
        if (log.isDebugEnabled()) {
            log.debug("Container add instance injector: {}", injector);
        }
        this.instanceInjectors.addLast(injector);
        return this;
    }

    public Container removeInstanceInjector(final InstanceInjector injector) {
        if (log.isDebugEnabled()) {
            log.debug("Container remove instance injector: {}", injector);
        }
        this.instanceInjectors.remove(injector);
        return this;
    }

    public Deque<InstanceInjector> getInstanceInjectors() {
        return instanceInjectors;
    }

    public Container addFirstParameterInjector(
        final ParameterInjector injector
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container add parameter injector: {}", injector);
        }
        this.parameterInjectors.addFirst(injector);
        return this;
    }

    public Container addParameterInjector(final ParameterInjector injector) {
        if (log.isDebugEnabled()) {
            log.debug("Container add parameter injector: {}", injector);
        }
        this.parameterInjectors.addLast(injector);
        return this;
    }

    public Container removeParameterInjector(final ParameterInjector injector) {
        if (log.isDebugEnabled()) {
            log.debug("Container remove parameter injector: {}", injector);
        }
        this.parameterInjectors.remove(injector);
        return this;
    }

    public Deque<ParameterInjector> getParameterInjectors() {
        return parameterInjectors;
    }

    public Container addFirstBeforeInjectProcessor(
        final BeforeInjectProcessor processor
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container add bean before processor: {}", processor);
        }
        this.beforeInjectProcessors.addFirst(processor);
        return this;
    }

    public Container addBeforeInjectProcessor(
        final BeforeInjectProcessor processor
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container add bean before processor: {}", processor);
        }
        this.beforeInjectProcessors.addLast(processor);
        return this;
    }

    public Container removeBeforeInjectProcessor(
        final BeforeInjectProcessor processor
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container remove bean before processor: {}", processor);
        }
        this.beforeInjectProcessors.remove(processor);
        return this;
    }

    public Deque<BeforeInjectProcessor> getBeforeInjectProcessors() {
        return beforeInjectProcessors;
    }

    public Container addFirstBeanAfterCreateProcessor(
        final BeanAfterCreateProcessor processor
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container add bean before processor: {}", processor);
        }
        this.beanAfterCreateProcessors.addFirst(processor);
        return this;
    }

    public Container addBeanAfterCreateProcessor(
        final BeanAfterCreateProcessor processor
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container add bean before processor: {}", processor);
        }
        this.beanAfterCreateProcessors.addLast(processor);
        return this;
    }

    public Container removeBeanAfterCreateProcessor(
        final BeanAfterCreateProcessor processor
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container remove bean before processor: {}", processor);
        }
        this.beanAfterCreateProcessors.remove(processor);
        return this;
    }

    public Deque<BeanAfterCreateProcessor> getBeanAfterCreateProcessors() {
        return beanAfterCreateProcessors;
    }

    public Container addFirstBeanDestroyProcessor(
        final BeanDestroyProcessor processor
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container add bean after processor: {}", processor);
        }
        this.beanDestroyProcessors.addFirst(processor);
        return this;
    }

    public Container addBeanDestroyProcessor(
        final BeanDestroyProcessor processor
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container add bean after processor: {}", processor);
        }
        this.beanDestroyProcessors.addLast(processor);
        return this;
    }

    public Container removeBeanDestroyProcessor(
        final BeanDestroyProcessor processor
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Container remove bean after processor: {}", processor);
        }
        this.beanDestroyProcessors.remove(processor);
        return this;
    }

    public Deque<BeanDestroyProcessor> getBeanDestroyProcessors() {
        return beanDestroyProcessors;
    }

    public void setInstanceValue(final String name, final Object instance) {
        final Binding binding = this.getBinding(name);
        if (binding == null) {
            throw new ContainerException("Target [" + name + "] not been bind");
        }
        binding.setSource(instance);
    }

    public void setInstanceValue(final Class<?> type, final Object instance) {
        this.setInstanceValue(this.getBeanNameByType(type), instance);
    }
}
