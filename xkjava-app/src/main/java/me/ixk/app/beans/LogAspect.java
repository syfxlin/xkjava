/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.beans;

import me.ixk.framework.annotation.core.Aspect;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.ProceedingJoinPoint;

@Aspect(pointcut = "@annotation(me.ixk.app.annotations.Log)")
public class LogAspect implements Advice {

    @Override
    public Object around(final ProceedingJoinPoint joinPoint) {
        System.out.println("Before-Log");
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("After-Log");
        return result;
    }
}
