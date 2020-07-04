/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import me.ixk.framework.exceptions.AspectProcessException;

public abstract class AbstractAdvice implements Advice {

    @Override
    public void before(JoinPoint joinPoint) {}

    @Override
    public void after(JoinPoint joinPoint) {}

    @Override
    public void afterReturning(JoinPoint joinPoint) {}

    @Override
    public void afterThrowing(Throwable exception) throws Throwable {
        throw exception;
    }

    @Override
    public Object around(ProceedingJoinPoint joinPoint) {
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
