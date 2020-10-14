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
    private final Object controller;
    private final Class<?> controllerClass;
    private final Method method;
    private final MergedAnnotation methodAnnotation;

    public MethodReturnValue(
        Object controller,
        Class<?> controllerClass,
        Method method,
        MergedAnnotation methodAnnotation
    ) {
        this.controller = controller;
        this.controllerClass = controllerClass;
        this.method = method;
        this.methodAnnotation = methodAnnotation;
    }

    public Object getController() {
        return controller;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getMethod() {
        return method;
    }

    public MergedAnnotation getMethodAnnotation() {
        return methodAnnotation;
    }
}
