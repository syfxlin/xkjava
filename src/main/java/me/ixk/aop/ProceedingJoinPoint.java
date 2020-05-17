package me.ixk.aop;

import net.sf.cglib.proxy.MethodProxy;

public class ProceedingJoinPoint extends JoinPoint {

    public ProceedingJoinPoint(
        AspectHandler handler,
        Object object,
        MethodProxy method,
        Object[] args
    ) {
        super(handler, object, method, args);
    }

    public Object proceed(Object... args) throws Throwable {
        return this.handler.invokeProcess(args);
    }
}
