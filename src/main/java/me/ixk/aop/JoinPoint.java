package me.ixk.aop;

import net.sf.cglib.proxy.MethodProxy;

public class JoinPoint {
    protected AspectHandler handler;

    protected Object[] args;

    protected Object object;

    protected Class<?> _class;

    protected MethodProxy method;

    protected Object _return;

    protected Throwable error;

    public JoinPoint(
        AspectHandler handler,
        Object object,
        MethodProxy method,
        Object[] args
    ) {
        this.handler = handler;
        this.args = args;
        this.object = object;
        this._class = object.getClass();
        this.method = method;
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

    public MethodProxy getMethod() {
        return method;
    }

    public void setMethod(MethodProxy method) {
        this.method = method;
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
}
