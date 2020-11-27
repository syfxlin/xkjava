/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import me.ixk.framework.exceptions.AspectProcessException;

/**
 * 通知
 * <p>
 * 本框架使用的是通过方法实现来进行通知的方法设置，而不使用注解的方式声明通知方法
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:09
 */
public interface Advice {
    /**
     * 前置通知
     *
     * @param joinPoint 连接点
     */
    default void before(JoinPoint joinPoint) {}

    /**
     * 后置通知
     *
     * @param joinPoint 连接点
     */
    default void after(JoinPoint joinPoint) {}

    /**
     * 后置通知（正常返回）
     *
     * @param joinPoint 连接点
     */
    default void afterReturning(JoinPoint joinPoint) {}

    /**
     * 后置通知（异常返回）
     *
     * @param exception 异常
     *
     * @throws Throwable 切面中如果没有处理异常则抛出
     */
    default void afterThrowing(Throwable exception) throws Throwable {
        throw exception;
    }

    /**
     * 环绕通知
     *
     * @param joinPoint 连接点（可运行）
     *
     * @return 切面返回值
     */
    default Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new AspectProcessException(
                "Around process has errors not captured",
                e
            );
        }
    }
}
