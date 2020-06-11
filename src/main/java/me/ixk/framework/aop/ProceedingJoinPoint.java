package me.ixk.framework.aop;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodProxy;

public class ProceedingJoinPoint extends JoinPoint {

    public ProceedingJoinPoint(
        AspectHandler handler,
        Object object,
        Method method,
        MethodProxy methodProxy,
        Object[] args
    ) {
        super(handler, object, method, methodProxy, args);
    }

    public Object proceed(Object... args) throws Throwable {
        return this.handler.invokeProcess(args);
    }
}
