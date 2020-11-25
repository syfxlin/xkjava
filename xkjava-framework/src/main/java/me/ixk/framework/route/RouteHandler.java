/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.middleware.Handler;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.middleware.Runner;

/**
 * 路由处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:51
 */
public class RouteHandler {
    private final Method method;
    private final Handler handler;
    private final List<Middleware> middlewares;

    public RouteHandler(
        final Method method,
        final Handler handler,
        final List<Middleware> middlewares
    ) {
        this.method = method;
        this.handler = handler;
        this.middlewares = middlewares;
    }

    public Response handle(
        final RouteResult result,
        final Request request,
        final Response response
    ) {
        try {
            this.handler.before(result, request, response);
            final Response value = new Runner(this.handler, this.middlewares)
            .then(request, response);
            this.handler.after(value, request, response);
            return this.handler.afterReturning(value, request, response);
        } catch (final Throwable e) {
            final Response res =
                this.handler.afterException(e, request, response);
            if (res != null) {
                return res;
            }
            throw e;
        }
    }

    public Method getMethod() {
        return method;
    }

    public Handler getHandler() {
        return handler;
    }

    public List<Middleware> getMiddlewares() {
        return middlewares;
    }
}
