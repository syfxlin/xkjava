/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.lang.SimpleCache;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.PostConstruct;
import me.ixk.framework.annotations.PreDestroy;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.exceptions.BindingException;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;

public class Binding {
    protected static final SimpleCache<Class<?>, BindingCache> BINDING_CACHE = new SimpleCache<>();

    private ScopeType scope;

    private Class<?> instanceType;

    private final BindingCache bindingCache;

    private Wrapper wrapper;

    private Object instance = NoCreated.class;

    public Binding(Wrapper wrapper, ScopeType scope) {
        this.wrapper = wrapper;
        this.scope = scope;
        this.bindingCache = new BindingCache();
    }

    public Binding(Wrapper wrapper, ScopeType scope, String instanceName) {
        this(wrapper, scope);
        this.setInstanceType(instanceName);
    }

    public Binding(Object instance, ScopeType scopeType) {
        this((container1, args) -> instance, scopeType);
        this.instance = instance;
    }

    public Binding(Object instance, ScopeType scopeType, String instanceName) {
        this((container1, args) -> instance, scopeType);
        this.setInstanceType(instanceName);
        this.instance = instance;
    }

    public ScopeType getScope() {
        return scope;
    }

    public void setScope(ScopeType scope) {
        this.scope = scope;
    }

    public Class<?> getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceName) {
        this.setInstanceType(ClassUtils.forName(instanceName));
    }

    public void setInstanceType(Class<?> instanceType) {
        this.instanceType = instanceType;
        if (instanceType != null) {
            BindingCache cache = BINDING_CACHE.get(instanceType);
            if (cache != null) {
                return;
            }
            List<Method> autowiredMethods = new ArrayList<>();
            for (Method method : instanceType.getDeclaredMethods()) {
                boolean postConstruct = AnnotationUtils.hasAnnotation(
                    method,
                    PostConstruct.class
                );
                if (postConstruct) {
                    bindingCache.setInitMethod(method);
                }
                boolean preDestroy = AnnotationUtils.hasAnnotation(
                    method,
                    PreDestroy.class
                );
                if (preDestroy) {
                    bindingCache.setDestroyMethods(method);
                }
                boolean autowired = AnnotationUtils.hasAnnotation(
                    method,
                    Autowired.class
                );
                if (autowired) {
                    autowiredMethods.add(method);
                }
            }
            bindingCache.setAutowiredMethod(autowiredMethods);
            BINDING_CACHE.put(instanceType, bindingCache);
        }
    }

    public Wrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    public Object getInstance() {
        if (this.instance == NoCreated.class) {
            throw new BindingException("Instance is not create or set");
        }
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public boolean isCreated() {
        return this.instance != NoCreated.class;
    }

    public boolean isSingleton() {
        return this.scope.isSingleton();
    }

    public boolean isPrototype() {
        return this.scope.isPrototype();
    }

    public boolean isRequest() {
        return this.scope.isRequest();
    }

    public Method getInitMethod() {
        return bindingCache.getInitMethod();
    }

    public void setInitMethod(Method initMethod) {
        bindingCache.setInitMethod(initMethod);
    }

    public Method getDestroyMethod() {
        return bindingCache.getDestroyMethod();
    }

    public void setDestroyMethod(Method destroyMethod) {
        bindingCache.setDestroyMethods(destroyMethod);
    }

    public List<Method> getAutowiredMethods() {
        return bindingCache.getAutowiredMethods();
    }

    public void setAutowiredMethods(List<Method> autowiredMethods) {
        bindingCache.setAutowiredMethod(autowiredMethods);
    }

    private static class NoCreated {}

    private static class BindingCache {
        private Method initMethod;
        private Method destroyMethod;
        private List<Method> autowiredMethods;
        private static final List<Method> EMPTY = new ArrayList<>();

        public Method getInitMethod() {
            return initMethod;
        }

        public void setInitMethod(Method initMethod) {
            this.initMethod = initMethod;
        }

        public Method getDestroyMethod() {
            return destroyMethod;
        }

        public void setDestroyMethods(Method destroyMethod) {
            this.destroyMethod = destroyMethod;
        }

        public List<Method> getAutowiredMethods() {
            return autowiredMethods == null ? EMPTY : autowiredMethods;
        }

        public void setAutowiredMethod(List<Method> autowiredMethods) {
            this.autowiredMethods = autowiredMethods;
        }
    }
}
