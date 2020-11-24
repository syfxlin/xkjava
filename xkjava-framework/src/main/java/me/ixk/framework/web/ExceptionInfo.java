/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.lang.reflect.Method;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 异常信息
 *
 * @author Otstar Lin
 * @date 2020/11/24 上午 9:34
 */
public class ExceptionInfo {
    private final Object controller;
    private final Class<?> controllerClass;
    private final Method method;
    private final MergedAnnotation methodAnnotation;
    private final Request request;
    private final Response response;

    public ExceptionInfo(
        Object controller,
        Class<?> controllerClass,
        Method method,
        MergedAnnotation methodAnnotation,
        Request request,
        Response response
    ) {
        this.controller = controller;
        this.controllerClass = controllerClass;
        this.method = method;
        this.methodAnnotation = methodAnnotation;
        this.request = request;
        this.response = response;
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

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}
