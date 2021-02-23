/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import net.sf.cglib.proxy.Enhancer;

/**
 * 代理创建器
 * <p>
 * 用于创建代理的对象
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:37
 */
public class ProxyCreator {

    /**
     * 创建普通代理类
     *
     * @param target     被代理的源对象
     * @param targetType 被代理对象的类型
     * @param interfaces 实现的接口
     * @param argsTypes  参数类型
     * @param args       参数
     * @return 代理后的对象
     */
    public static Object createProxy(
        final Object target,
        final Class<?> targetType,
        final Class<?>[] interfaces,
        final Class<?>[] argsTypes,
        final Object[] args
    ) {
        return createAop(null, target, targetType, interfaces, argsTypes, args);
    }

    /**
     * 创建切面代理类
     *
     * @param aspectManager 切面管理器
     * @param target        被代理的源对象
     * @param targetType    被代理对象的类型
     * @param interfaces    实现的接口
     * @param argsTypes     参数类型
     * @param args          参数
     * @return 代理后的对象
     */
    public static Object createAop(
        final AspectManager aspectManager,
        final Object target,
        final Class<?> targetType,
        final Class<?>[] interfaces,
        final Class<?>[] argsTypes,
        final Object[] args
    ) {
        final TargetSource targetSource = new TargetSource(
            aspectManager,
            target,
            targetType,
            interfaces
        );
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetSource.getTargetType());
        enhancer.setInterfaces(targetSource.getInterfaces());
        enhancer.setCallback(new DynamicInterceptor(targetSource));
        return enhancer.create(argsTypes, args);
    }
}
