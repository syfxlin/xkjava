/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import me.ixk.framework.util.MergedAnnotation;
import me.ixk.framework.util.ParameterNameDiscoverer;

/**
 * 方法参数
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:17
 */
public class MethodParameter {

    private final Object handler;
    private final Method method;
    private final Parameter[] parameters;
    private final String[] parameterNames;
    private final MergedAnnotation methodAnnotation;
    private final MergedAnnotation[] parameterAnnotations;
    private volatile int parameterIndex;

    public MethodParameter(final Object handler, final Method method) {
        this.handler = handler;
        this.method = method;
        this.parameters = method.getParameters();
        this.parameterNames = ParameterNameDiscoverer.getParameterNames(method);
        this.methodAnnotation = MergedAnnotation.from(method);
        this.parameterAnnotations =
            Arrays
                .stream(this.parameters)
                .map(MergedAnnotation::from)
                .toArray(MergedAnnotation[]::new);
    }

    public int getParameterIndex() {
        if (parameterIndex == -1) {
            throw new UnsupportedOperationException(
                "Unsupported get parameter in this"
            );
        }
        return parameterIndex;
    }

    public void setParameterIndex(final int parameterIndex) {
        this.parameterIndex = parameterIndex;
    }

    public Object getHandler() {
        return handler;
    }

    public Method getMethod() {
        return method;
    }

    public Parameter getParameter() {
        return parameters[this.getParameterIndex()];
    }

    public String getParameterName() {
        return parameterNames[this.getParameterIndex()];
    }

    public MergedAnnotation getMethodAnnotation() {
        return methodAnnotation;
    }

    public MergedAnnotation getParameterAnnotation() {
        return parameterAnnotations[this.getParameterIndex()];
    }

    public Class<?> getParameterType() {
        return this.getParameter().getType();
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public MergedAnnotation[] getParameterAnnotations() {
        return parameterAnnotations;
    }
}
