/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.lang.reflect.Method;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.util.MergedAnnotation;

/**
 * 异常信息
 *
 * @author Otstar Lin
 * @date 2020/11/24 上午 9:34
 */
public class ExceptionInfo {

    private final Object handler;
    private final Class<?> handlerType;
    private final Method method;
    private final MergedAnnotation methodAnnotation;
    private final Request request;
    private final Response response;

    public ExceptionInfo(
        final Object handler,
        final Class<?> handlerType,
        final Method method,
        final Request request,
        final Response response
    ) {
        this.handler = handler;
        this.handlerType = handlerType;
        this.method = method;
        this.methodAnnotation = MergedAnnotation.from(method);
        this.request = request;
        this.response = response;
    }

    public Object getHandler() {
        return handler;
    }

    public Class<?> getHandlerType() {
        return handlerType;
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
