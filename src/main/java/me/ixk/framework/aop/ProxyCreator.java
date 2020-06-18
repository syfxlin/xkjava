package me.ixk.framework.aop;

import net.sf.cglib.proxy.Enhancer;

public class ProxyCreator {

    public static Object createProxy(
        Object target,
        Class<?> targetType,
        Class<?>[] instances
    ) {
        TargetSource targetSource = new TargetSource(
            target,
            targetType,
            instances
        );
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetSource.getTargetType());
        enhancer.setInterfaces(targetSource.getInterfaces());
        enhancer.setCallback(new DynamicInterceptor(targetSource));
        return enhancer.create();
    }
}
