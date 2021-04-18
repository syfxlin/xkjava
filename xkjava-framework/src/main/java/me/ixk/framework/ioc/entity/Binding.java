/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */
package me.ixk.framework.ioc.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.PostConstruct;
import me.ixk.framework.annotation.core.PreDestroy;
import me.ixk.framework.annotation.core.Primary;
import me.ixk.framework.ioc.context.Context;
import me.ixk.framework.ioc.factory.FactoryBean;
import me.ixk.framework.ioc.factory.ObjectFactory;
import me.ixk.framework.util.MergedAnnotation;
import me.ixk.framework.util.ReflectUtils;
import me.ixk.framework.util.SoftCache;

/**
 * Binding
 *
 * @author Otstar Lin
 * @date 2020/10/25 下午 9:02
 */
public class Binding {

    private static final SoftCache<Class<?>, BindingInfos> CACHE = new SoftCache<>();

    private final Context context;
    private final String scope;
    private final String name;
    private final AnnotatedEntry<Class<?>> instanceTypeEntry;
    private final boolean primary;
    private final BindingInfos bindingInfos;

    private final FactoryBean<?> factoryBean;
    /**
     * 用于锁住当前 Binding 的代理锁
     */
    private final Object mutex = new Object();

    public Binding(
        final Context context,
        final String name,
        final Class<?> instanceType,
        final String scopeType
    ) {
        this(context, name, instanceType, scopeType, null);
    }

    public Binding(
        final Context context,
        final String name,
        final Class<?> instanceType,
        final String scopeType,
        final FactoryBean<?> factoryBean
    ) {
        this.context = context;
        this.scope = scopeType;
        this.name = name;
        this.instanceTypeEntry = new AnnotatedEntry<>(instanceType);
        this.primary = this.getAnnotation().hasAnnotation(Primary.class);
        this.bindingInfos =
            CACHE.computeIfAbsent(instanceType, BindingInfos::new);
        this.factoryBean = factoryBean;
    }

    public Binding(
        final Context context,
        final String name,
        final Object instance,
        final String scopeType
    ) {
        this(context, name, instance.getClass(), scopeType);
        this.setSource(instance);
    }

    public Binding(
        final Context context,
        final String name,
        final FactoryBean<?> factoryBean,
        final String scopeType
    ) {
        this(
            context,
            name,
            factoryBean.getObjectType(),
            scopeType,
            factoryBean
        );
    }

    public String getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return this.instanceTypeEntry.getElement();
    }

    public MergedAnnotation getAnnotation() {
        return this.instanceTypeEntry.getAnnotation();
    }

    public AnnotatedEntry<Class<?>> getInstanceTypeEntry() {
        return instanceTypeEntry;
    }

    public boolean isPrimary() {
        return primary;
    }

    public Object getSource() {
        final Object source = this.getSourceUnsafe();
        if (source == null) {
            synchronized (this.getMutex()) {
                return this.getSourceUnsafe();
            }
        }
        return source;
    }

    public Object getSource(final boolean proxy) {
        if (proxy && this.useProxy() && this.isShared()) {
            return ReflectUtils.proxyObjectFactory(
                (ObjectFactory<Object>) this::getSource,
                this.getType()
            );
        } else {
            return this.getSource();
        }
    }

    private Object getSourceUnsafe() {
        return this.isCreated() ? this.context.get(name) : null;
    }

    public void setSource(final Object instance) {
        synchronized (this.getMutex()) {
            if (this.context.isShared()) {
                this.context.set(name, instance);
            }
        }
    }

    public FactoryBean<?> getFactoryBean() {
        return factoryBean;
    }

    public boolean isCreated() {
        final boolean has = this.context.has(name);
        if (!has) {
            synchronized (this.getMutex()) {
                return this.context.has(name);
            }
        }
        return true;
    }

    public boolean isShared() {
        return this.context.isShared();
    }

    public boolean useProxy() {
        return this.context.useProxy();
    }

    public Object getMutex() {
        return this.mutex;
    }

    public List<Method> getInitMethods() {
        return this.bindingInfos.getInitMethods();
    }

    public List<Method> getDestroyMethods() {
        return this.bindingInfos.getDestroyMethods();
    }

    public List<Method> getAutowiredMethods() {
        return this.bindingInfos.getAutowiredMethods();
    }

    public AnnotatedEntry<Field>[] getFieldEntries() {
        return this.bindingInfos.getFieldEntries();
    }

    public AnnotatedEntry<Method>[] getMethodEntries() {
        return this.bindingInfos.getMethodEntries();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Binding binding = (Binding) o;
        return Objects.equals(name, binding.name);
    }

    private static class BindingInfos {

        private final List<Method> initMethods = new ArrayList<>();
        private final List<Method> destroyMethods = new ArrayList<>();
        private final List<Method> autowiredMethods = new ArrayList<>();

        private final AnnotatedEntry<Field>[] fieldEntries;
        private final AnnotatedEntry<Method>[] methodEntries;

        @SuppressWarnings("unchecked")
        public BindingInfos(final Class<?> instanceType) {
            // Fields
            this.fieldEntries =
                Arrays
                    .stream(instanceType.getDeclaredFields())
                    .map(AnnotatedEntry::new)
                    .toArray(AnnotatedEntry[]::new);
            // Methods
            this.methodEntries =
                Arrays
                    .stream(instanceType.getDeclaredMethods())
                    .map(AnnotatedEntry::new)
                    .toArray(AnnotatedEntry[]::new);
            // InitMethod, DestroyMethod, AutowiredMethod
            for (final AnnotatedEntry<Method> entry : this.methodEntries) {
                final Method method = entry.getElement();
                final MergedAnnotation annotation = entry.getAnnotation();
                if (annotation.hasAnnotation(PostConstruct.class)) {
                    initMethods.add(method);
                }
                if (annotation.hasAnnotation(PreDestroy.class)) {
                    destroyMethods.add(method);
                }
                if (annotation.hasAnnotation(Autowired.class)) {
                    autowiredMethods.add(method);
                }
            }
        }

        public List<Method> getInitMethods() {
            return initMethods;
        }

        public List<Method> getDestroyMethods() {
            return destroyMethods;
        }

        public List<Method> getAutowiredMethods() {
            return autowiredMethods;
        }

        public AnnotatedEntry<Field>[] getFieldEntries() {
            return fieldEntries;
        }

        public AnnotatedEntry<Method>[] getMethodEntries() {
            return methodEntries;
        }
    }
}
