package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.List;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class DynamicInterceptor implements MethodInterceptor {
    protected final TargetSource targetSource;

    public DynamicInterceptor(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    @Override
    public Object intercept(
        Object object,
        Method method,
        Object[] args,
        MethodProxy methodProxy
    )
        throws Throwable {
        List<Advice> advices = this.targetSource.getAdvices(method);
        Object target = this.targetSource.getTarget();
        if (advices != null && !advices.isEmpty()) {
            AspectHandler handler = new AspectHandler(
                target,
                method,
                methodProxy,
                args,
                0,
                advices
            );
            return handler.invokeAspect();
        }

        return methodProxy.invoke(target, args);
    }
}
