package me.ixk.framework.ioc;

import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.exceptions.BindingException;
import me.ixk.framework.utils.ClassUtils;

public class Binding {
    private ScopeType scope;

    private Class<?> instanceType;

    private String initMethodName;
    private String destroyMethodName;

    private Wrapper wrapper;

    private Object instance = NoCreated.class;

    public Binding(Wrapper wrapper, ScopeType scope) {
        this.wrapper = wrapper;
        this.scope = scope;
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
        //        for (Method method : instanceType.getDeclaredMethods()) {
        //            PostConstruct postConstruct = method.getAnnotation(
        //                PostConstruct.class
        //            );
        //            if (postConstruct != null) {
        //                this.initMethodName = method.getName();
        //            }
        //            PreDestroy preDestroy = method.getAnnotation(PreDestroy.class);
        //            if (preDestroy != null) {
        //                this.destroyMethodName = method.getName();
        //            }
        //        }
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

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    private static class NoCreated {}
}
