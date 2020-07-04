/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class JoinPoint {
    protected final AspectHandler handler;

    protected Object[] args;

    protected Object object;

    protected Class<?> _class;

    protected Method method;

    protected MethodProxy methodProxy;

    protected Object _return;

    protected Throwable error;

    public JoinPoint(
        AspectHandler handler,
        Object object,
        Method method,
        MethodProxy methodProxy,
        Object[] args
    ) {
        this.handler = handler;
        this.args = args;
        this.object = object;
        this._class = object.getClass();
        this.method = method;
        this.methodProxy = methodProxy;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Class<?> getTargetClass() {
        return _class;
    }

    public void setTargetClass(Class<?> _class) {
        this._class = _class;
    }

    public MethodProxy getMethodProxy() {
        return methodProxy;
    }

    public void setMethodProxy(MethodProxy methodProxy) {
        this.methodProxy = methodProxy;
    }

    public Object getReturn() {
        return _return;
    }

    public void setReturn(Object _return) {
        this._return = _return;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
