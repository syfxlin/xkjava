package me.ixk.aop;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DynamicInterceptor implements MethodInterceptor {
    protected Map<String, List<AdviceInterface>> aspects;

    public DynamicInterceptor(Map<String, List<AdviceInterface>> aspects) {
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
                methodProxy,
                objects,
                aspects.get(method.getName())
            );
            return handler.invokeAspect();
        }

        return methodProxy.invokeSuper(o, objects);
    }
}
