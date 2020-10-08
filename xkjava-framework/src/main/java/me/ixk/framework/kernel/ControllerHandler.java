/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.exceptions.DispatchServletException;
import me.ixk.framework.exceptions.Exception;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.WebDataBinder;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(
        ControllerHandler.class
    );

    private Class<?> controllerClass;
    private final String methodName;

    private final XkJava app;

    private static final String NO_RESOLVER = "NO_RESOLVER";

    public ControllerHandler(String handler) {
        String[] handlerArr = handler.split("@");
        try {
            this.controllerClass = Class.forName(handlerArr[0]);
        } catch (ClassNotFoundException e) {
            this.controllerClass = null;
        }
        this.methodName = handlerArr[1];
        this.app = XkJava.of();
    }

    @Override
    public Object handle(Request request) {
        // 将控制器信息注入 RequestContext
        this.app.setAttribute(
                "controllerClass",
                this.controllerClass,
                ScopeType.REQUEST
            );
        this.app.setAttribute(
                "controllerMethod",
                this.methodName,
                ScopeType.REQUEST
            );
        try {
            WebDataBinder webDataBinder = new WebDataBinder(this.app, request);
            Object controller =
                this.app.make(this.controllerClass, webDataBinder);
            this.processInitBinder(webDataBinder);
            return this.app.call(
                    controller,
                    this.methodName,
                    Object.class,
                    webDataBinder
                );
        } catch (Throwable e) {
            logger.error("ControllerHandler Exception", e);
            // 处理 ExceptionHandler 注解定义的错误处理器
            Object result = this.processException(e);
            if (NO_RESOLVER.equals(result)) {
                // 若错误未能解决，或者产生了新的错误则向上抛出
                throw e;
            }
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    protected void processInitBinder(WebDataBinder binder) {
        Map<String, Object> args = new ConcurrentHashMap<>();
        args.put("binder", binder);
        args.put("webDataBinder", binder);
        args.put("dataBinder", binder);
        args.put(WebDataBinder.class.getName(), binder);
        args.put(DataBinder.class.getName(), binder);
        Map<Class<?>, InitBinderHandlerResolver> controllerResolvers =
            this.app.getAttribute(
                    "controllerInitBinderHandlerResolver",
                    Map.class
                );
        InitBinderHandlerResolver resolver = controllerResolvers.get(
            this.controllerClass
        );
        if (resolver != null) {
            for (Method method : resolver.resolveMethods()) {
                this.app.call(this.controllerClass, method, Object.class, args);
            }
        }
        List<InitBinderHandlerResolver> handlerResolvers =
            this.app.getAttribute(
                    "adviceInitBinderHandlerResolver",
                    List.class
                );
        for (InitBinderHandlerResolver handlerResolver : handlerResolvers) {
            for (Method method : handlerResolver.resolveMethods()) {
                this.app.call(this.controllerClass, method, Object.class, args);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Object processException(Throwable exception) {
        Object result = NO_RESOLVER;
        Map<Class<?>, ExceptionHandlerResolver> controllerResolvers =
            this.app.getAttribute(
                    "controllerExceptionHandlerResolvers",
                    Map.class
                );
        ExceptionHandlerResolver resolver = controllerResolvers.get(
            this.controllerClass
        );
        if (resolver != null) {
            result =
                this.processException(
                        exception,
                        this.controllerClass,
                        resolver
                    );
            if (!result.equals(NO_RESOLVER)) {
                return result;
            }
        }
        Map<Class<?>, ExceptionHandlerResolver> handlerResolvers =
            this.app.getAttribute("adviceExceptionHandlerResolvers", Map.class);
        for (Map.Entry<Class<?>, ExceptionHandlerResolver> entry : handlerResolvers.entrySet()) {
            result =
                this.processException(
                        exception,
                        entry.getKey(),
                        entry.getValue()
                    );
            if (!result.equals(NO_RESOLVER)) {
                return result;
            }
        }
        return result;
    }

    protected Object processException(
        Throwable exception,
        Class<?> clazz,
        ExceptionHandlerResolver resolver
    ) {
        try {
            Method method = resolver.resolveMethod(exception);
            if (method != null) {
                // 绑定可能注入的异常
                Map<String, Object> args = new ConcurrentHashMap<>();
                args.put("exception", exception);
                args.put(exception.getClass().getName(), exception);
                args.put(Throwable.class.getName(), exception);
                args.put(Exception.class.getName(), exception);
                args.put(Exception.class.getName(), exception);
                // 获取返回值
                return this.app.call(clazz, method, Object.class, args);
            }
        } catch (Throwable e) {
            throw new DispatchServletException(
                "Process ExceptionHandlerResolver failed",
                e
            );
        }
        return NO_RESOLVER;
    }
}
