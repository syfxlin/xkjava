/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.util.ClassUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.PostConstruct;
import me.ixk.framework.annotations.PreDestroy;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.utils.AnnotationUtils;
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
    private static final SimpleCache<Class<?>, BindingMethods> CACHE = new SimpleCache<>();

    private final Context context;
    private volatile Wrapper wrapper;
    private final ScopeType scope;
    private final String instanceName;
    private final Class<?> instanceType;
    private volatile BindingMethods bindingMethods;

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

    private void init() {
        if (instanceType != null) {
            final BindingMethods cache = CACHE.get(instanceType);
            if (cache != null) {
                this.bindingMethods = cache;
                return;
            } else {
                this.bindingMethods = new BindingMethods();
            }
            final List<Method> autowiredMethods = new ArrayList<>();
            for (final Method method : instanceType.getDeclaredMethods()) {
                final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
                    method
                );
                if (annotation.hasAnnotation(PostConstruct.class)) {
                    this.bindingMethods.setInitMethod(method);
                }
                if (annotation.hasAnnotation(PreDestroy.class)) {
                    this.bindingMethods.setDestroyMethod(method);
                }
                if (annotation.hasAnnotation(Autowired.class)) {
                    autowiredMethods.add(method);
                }
            }
            this.bindingMethods.setAutowiredMethods(autowiredMethods);
            CACHE.put(instanceType, this.bindingMethods);
        } else {
            this.bindingMethods = new BindingMethods();
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
        return this.isCreated() ? this.context.get(instanceName) : null;
    }

    public void setInstance(Object instance) {
        this.context.set(instanceName, instance);
    }

    public boolean isCreated() {
        return this.context.has(instanceName);
    }

    public Wrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    public Method getInitMethod() {
        return this.bindingMethods.getInitMethod();
    }

    public void setInitMethod(final Method initMethod) {
        this.bindingMethods.setInitMethod(initMethod);
    }

    public Method getDestroyMethod() {
        return this.bindingMethods.getDestroyMethod();
    }

    public void setDestroyMethod(final Method destroyMethod) {
        this.bindingMethods.setDestroyMethod(destroyMethod);
    }

    public List<Method> getAutowiredMethods() {
        return this.bindingMethods.getAutowiredMethods();
    }

    public void setAutowiredMethods(final List<Method> autowiredMethods) {
        this.bindingMethods.setAutowiredMethods(autowiredMethods);
    }

    private static class BindingMethods {
        private volatile Method initMethod;
        private volatile Method destroyMethod;
        private volatile List<Method> autowiredMethods;

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
    }

    private static class NoCreated {}
}
