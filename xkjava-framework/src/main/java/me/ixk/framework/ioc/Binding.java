/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.util.ClassUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.PostConstruct;
import me.ixk.framework.annotations.PreDestroy;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.utils.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Binding
 *
 * @author Otstar Lin
 * @date 2020/10/25 下午 9:02
 */
public class Binding {
    private static final Logger log = LoggerFactory.getLogger(Binding.class);
    private static final SimpleCache<Class<?>, BindingInfos> CACHE = new SimpleCache<>();

    private final Context context;
    private volatile Wrapper wrapper;
    private final ScopeType scope;
    private final String instanceName;
    private final Class<?> instanceType;
    private volatile BindingInfos bindingInfos;

    public Binding(
        final Context context,
        final String instanceName,
        final Wrapper wrapper,
        final ScopeType scopeType
    ) {
        this.context = context;
        this.wrapper = wrapper;
        this.scope = scopeType;
        this.instanceName = instanceName;
        Class<?> type;
        try {
            type = ClassUtil.loadClass(instanceName);
        } catch (final UtilException e) {
            type = null;
        }
        this.instanceType = type;
        this.init();
    }

    public Binding(
        final Context context,
        final String instanceName,
        final Object instance,
        final ScopeType scopeType
    ) {
        this(context, instanceName, null, scopeType);
        this.setInstance(instance);
        this.setWrapper((container, with) -> this.getInstance());
    }

    @SuppressWarnings("unchecked")
    private void init() {
        if (instanceType != null) {
            final BindingInfos cache = CACHE.get(instanceType);
            if (cache != null) {
                this.bindingInfos = cache;
                return;
            } else {
                this.bindingInfos = new BindingInfos();
            }
            // Fields
            this.bindingInfos.setFieldEntries(
                    Arrays
                        .stream(instanceType.getDeclaredFields())
                        .map(InjectorEntry::new)
                        .toArray(InjectorEntry[]::new)
                );
            // Methods
            this.bindingInfos.setMethodEntries(
                    Arrays
                        .stream(instanceType.getDeclaredMethods())
                        .map(InjectorEntry::new)
                        .toArray(InjectorEntry[]::new)
                );
            // InitMethod, DestroyMethod, AutowiredMethod
            final List<Method> autowiredMethods = new ArrayList<>();
            for (InjectorEntry<Method> entry : this.bindingInfos.getMethodEntries()) {
                final Method method = entry.getElement();
                final MergedAnnotation annotation = entry.getAnnotation();
                if (annotation.hasAnnotation(PostConstruct.class)) {
                    this.bindingInfos.setInitMethod(method);
                }
                if (annotation.hasAnnotation(PreDestroy.class)) {
                    this.bindingInfos.setDestroyMethod(method);
                }
                if (annotation.hasAnnotation(Autowired.class)) {
                    autowiredMethods.add(method);
                }
            }
            this.bindingInfos.setAutowiredMethods(autowiredMethods);
            CACHE.put(instanceType, this.bindingInfos);
        } else {
            this.bindingInfos = new BindingInfos();
        }
    }

    public ScopeType getScope() {
        return scope;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public Class<?> getInstanceType() {
        return instanceType;
    }

    public Object getInstance() {
        return this.isCreated()
            ? this.context.get(
                    instanceName,
                    instanceType == null ? Object.class : instanceType
                )
            : null;
    }

    public void setInstance(final Object instance) {
        this.context.set(instanceName, instance);
    }

    public boolean isCreated() {
        return this.context.has(instanceName);
    }

    public Wrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(final Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    public Method getInitMethod() {
        return this.bindingInfos.getInitMethod();
    }

    public void setInitMethod(final Method initMethod) {
        this.bindingInfos.setInitMethod(initMethod);
    }

    public Method getDestroyMethod() {
        return this.bindingInfos.getDestroyMethod();
    }

    public void setDestroyMethod(final Method destroyMethod) {
        this.bindingInfos.setDestroyMethod(destroyMethod);
    }

    public List<Method> getAutowiredMethods() {
        return this.bindingInfos.getAutowiredMethods();
    }

    public void setAutowiredMethods(final List<Method> autowiredMethods) {
        this.bindingInfos.setAutowiredMethods(autowiredMethods);
    }

    public InjectorEntry<Field>[] getFieldEntries() {
        return this.bindingInfos.getFieldEntries();
    }

    public InjectorEntry<Method>[] getMethodEntries() {
        return this.bindingInfos.getMethodEntries();
    }

    private static class BindingInfos {
        private volatile Method initMethod;
        private volatile Method destroyMethod;
        private volatile List<Method> autowiredMethods;

        private volatile InjectorEntry<Field>[] fieldEntries;
        private volatile InjectorEntry<Method>[] methodEntries;

        public Method getInitMethod() {
            return initMethod;
        }

        public void setInitMethod(final Method initMethod) {
            this.initMethod = initMethod;
        }

        public Method getDestroyMethod() {
            return destroyMethod;
        }

        public void setDestroyMethod(final Method destroyMethod) {
            this.destroyMethod = destroyMethod;
        }

        public List<Method> getAutowiredMethods() {
            return autowiredMethods == null
                ? Collections.emptyList()
                : autowiredMethods;
        }

        public void setAutowiredMethods(final List<Method> autowiredMethods) {
            this.autowiredMethods = autowiredMethods;
        }

        public InjectorEntry<Field>[] getFieldEntries() {
            return fieldEntries;
        }

        public void setFieldEntries(InjectorEntry<Field>[] fieldEntries) {
            this.fieldEntries = fieldEntries;
        }

        public InjectorEntry<Method>[] getMethodEntries() {
            return methodEntries;
        }

        public void setMethodEntries(InjectorEntry<Method>[] methodEntries) {
            this.methodEntries = methodEntries;
        }
    }

    private static class NoCreated {}
}
