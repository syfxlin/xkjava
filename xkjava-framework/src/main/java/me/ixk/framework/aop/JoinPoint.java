/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import java.lang.reflect.Method;
import me.ixk.framework.aop.AspectHandler.TargetInfo;
import me.ixk.framework.ioc.entity.ParameterContext.ParameterEntry;
import me.ixk.framework.util.MergedAnnotation;

/**
 * 连接点
 * <p>
 * 存储一些切面的信息
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:32
 */
public class JoinPoint {

    protected final AspectHandler handler;

    protected final TargetInfo info;

    /**
     * 返回值
     */
    protected Object returnValue;

    /**
     * 抛出的异常
     */
    protected Throwable error;

    public JoinPoint(final AspectHandler handler, final TargetInfo info) {
        this.handler = handler;
        this.info = info;
    }

    public Object[] getArgs() {
        return this.info.getArgs();
    }

    public Object getTarget() {
        return this.info.getTarget();
    }

    public Class<?> getTargetClass() {
        return this.info.getClazz();
    }

    public Object getReturn() {
        return returnValue;
    }

    public void setReturn(final Object returnValue) {
        this.returnValue = returnValue;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(final Throwable error) {
        this.error = error;
    }

    public Method getMethod() {
        return this.info.getMethod();
    }

    public MergedAnnotation getMethodAnnotation() {
        return this.info.getMethodAnnotation();
    }

    public MergedAnnotation getClassAnnotation() {
        return this.info.getClassAnnotation();
    }

    public ParameterEntry[] getParameterEntries() {
        return this.info.getParameterEntries();
    }

    public TargetInfo getInfo() {
        return info;
    }
}
