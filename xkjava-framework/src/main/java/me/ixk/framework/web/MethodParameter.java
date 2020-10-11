/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;

public class MethodParameter {
    private final Object controller;
    private final Class<?> controllerClass;
    private final Method method;
    private final Parameter[] parameters;
    private final String[] parameterNames;
    private final MergedAnnotation methodAnnotation;
    private final MergedAnnotation[] parameterAnnotations;
    private int parameterIndex;

    public MethodParameter(
        Object controller,
        Class<?> controllerClass,
        Method method,
        Parameter[] parameter,
        String[] parameterName,
        MergedAnnotation methodAnnotation
    ) {
        this.controller = controller;
        this.controllerClass = controllerClass;
        this.method = method;
        this.parameters = parameter;
        this.parameterNames = parameterName;
        this.methodAnnotation = methodAnnotation;
        this.parameterAnnotations =
            Arrays
                .stream(parameter)
                .map(AnnotationUtils::getAnnotation)
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

    public void setParameterIndex(int parameterIndex) {
        this.parameterIndex = parameterIndex;
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
