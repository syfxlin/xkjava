/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.exceptions.Exception;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Handler;
import me.ixk.framework.registry.after.WebResolverRegistry;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.utils.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础异常控制器
 *
 * @author Otstar Lin
 * @date 2020/11/25 上午 10:34
 */
@Component(name = "basicErrorHandler")
public class BasicErrorHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(
        BasicErrorHandler.class
    );
    private final XkJava app;
    private final WebResolverRegistry registry;

    public BasicErrorHandler() {
        this.app = XkJava.of();
        this.registry = this.app.make(WebResolverRegistry.class);
    }

    @Override
    public void before(Request request, Response response, RouteInfo result) {
        throw new RuntimeException("BasicErrorHandler no handler");
    }

    @Override
    public Object handle(Request request, Response response, RouteInfo info) {
        throw new RuntimeException("BasicErrorHandler no handler");
    }

    @Override
    public Response afterException(
        Throwable e,
        Request request,
        Response response,
        RouteInfo info
    ) {
        if (e instanceof InvocationTargetException) {
            e = ((InvocationTargetException) e).getTargetException();
        }
        log.error("ControllerHandler Exception [After]", e);
        // 处理后置异常处理器
        final Response result =
            this.processAfterException(e, request, response);
        if (result == null) {
            // 若错误未能解决，或者产生了新的错误则向上抛出
            throw new Exception(e);
        }
        return result;
    }

    @Override
    public Response afterReturning(
        Object returnValue,
        Request request,
        Response response,
        RouteInfo info
    ) {
        throw new RuntimeException("BasicErrorHandler no handler");
    }

    private Response processAfterException(
        final Throwable exception,
        final Request request,
        final Response response
    ) {
        final Class<? extends BasicErrorHandler> clazz = this.getClass();
        final Method method = ReflectUtil.getMethod(
            clazz,
            "handle",
            Request.class,
            Response.class
        );
        final MergedAnnotation methodAnnotation = MergedAnnotation.from(method);
        final ExceptionInfo info = new ExceptionInfo(
            this,
            clazz,
            method,
            methodAnnotation,
            request,
            response
        );
        for (final AfterHandlerExceptionResolver resolver : registry.getAfterHandlerExceptionResolvers()) {
            final Response result = resolver.resolveException(
                exception,
                info,
                this.app.make(WebContext.class),
                new WebDataBinder(this.app, request)
            );
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
