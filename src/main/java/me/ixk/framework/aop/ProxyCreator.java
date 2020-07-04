/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import net.sf.cglib.proxy.Enhancer;

public class ProxyCreator {

    public static Object createProxy(
        Object target,
        Class<?> targetType,
        Class<?>[] interfaces,
        Class<?>[] argsTypes,
        Object[] args
    ) {
        TargetSource targetSource = new TargetSource(
            target,
            targetType,
            interfaces
        );
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetSource.getTargetType());
        enhancer.setInterfaces(targetSource.getInterfaces());
        enhancer.setCallback(new DynamicInterceptor(targetSource));
        return enhancer.create(argsTypes, args);
    }
}
