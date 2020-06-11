package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class DynamicInterceptor implements MethodInterceptor {
    protected Map<String, List<Advice>> aspects;

    public DynamicInterceptor(Map<String, List<Advice>> aspects) {
        this.aspects = aspects;
    }

    @Override
    public Object intercept(
        Object o,
        Method method,
        Object[] objects,
        MethodProxy methodProxy
    )
        throws Throwable {
        if (this.aspects.containsKey(method.getName())) {
            AspectHandler handler = new AspectHandler(
                o,
                method,
                methodProxy,
                objects,
                aspects.get(method.getName())
            );
            return handler.invokeAspect();
        }

        return methodProxy.invokeSuper(o, objects);
    }
}
