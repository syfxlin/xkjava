/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import java.util.Collections;
import java.util.List;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.servlet.InvocableHandlerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 中间件执行链
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:44
 */
public class HandlerMiddlewareChain {

    private static final Logger log = LoggerFactory.getLogger(
        HandlerMiddlewareChain.class
    );
    protected final InvocableHandlerMethod handler;
    protected final List<Middleware> middleware;

    public HandlerMiddlewareChain(final InvocableHandlerMethod handler) {
        this.handler = handler;
        final List<Middleware> middlewares = handler.getMiddlewares();
        this.middleware =
            middlewares == null ? Collections.emptyList() : middlewares;
    }

    public boolean applyBeforeHandle(
        final Request request,
        final Response response
    ) throws Exception {
        for (final Middleware middleware : this.middleware) {
            if (!middleware.beforeHandle(request, response)) {
                this.triggerAfterCompletion(request, response);
                return false;
            }
        }
        return true;
    }

    public Object applyAfterHandle(
        Object returnValue,
        final Request request,
        final Response response
    ) throws Exception {
        for (int i = this.middleware.size() - 1; i >= 0; i--) {
            final Middleware middleware = this.middleware.get(i);
            returnValue =
                middleware.afterHandle(returnValue, request, response);
        }
        return returnValue;
    }

    public void triggerAfterCompletion(
        final Request request,
        final Response response
    ) throws Exception {
        for (final Middleware middleware : this.middleware) {
            try {
                middleware.afterCompletion(request, response);
            } catch (final Throwable th) {
                log.error(
                    "HandlerInterceptor.afterCompletion threw exception",
                    th
                );
            }
        }
    }

    public Object handle(final Request request, final Response response)
        throws Exception {
        return this.handler.invokeForRequest(request, response);
    }
}
