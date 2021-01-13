/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.lang.reflect.Method;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 方法返回值
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:17
 */
public class MethodReturnValue {

    private final Object handler;
    private final Class<?> handlerType;
    private final Method method;
    private final MergedAnnotation methodAnnotation;

    public MethodReturnValue(
        final Object handler,
        final Class<?> handlerType,
        final Method method
    ) {
        this.handler = handler;
        this.handlerType = handlerType;
        this.method = method;
        this.methodAnnotation = MergedAnnotation.from(method);
    }

    public Object getHandler() {
        return handler;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getHandlerType() {
        return handlerType;
    }

    public MergedAnnotation getMethodAnnotation() {
        return methodAnnotation;
    }
}
