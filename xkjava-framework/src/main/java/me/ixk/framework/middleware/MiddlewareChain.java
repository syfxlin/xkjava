/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.route.RouteInfo;

/**
 * 中间件执行链
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:44
 */
public class MiddlewareChain {

    protected final Handler handler;
    protected final Queue<Middleware> middleware;
    protected final RouteInfo info;

    public MiddlewareChain(
        final Handler handler,
        final List<Middleware> middleware,
        final RouteInfo info
    ) {
        this(handler, (Queue<Middleware>) new LinkedList<>(middleware), info);
    }

    public MiddlewareChain(
        final Handler handler,
        final Queue<Middleware> middleware,
        final RouteInfo info
    ) {
        this.handler = handler;
        this.middleware = middleware;
        this.info = info;
    }

    public Object handle(final Request request, final Response response) {
        final Middleware middleware = this.middleware.poll();
        if (middleware == null) {
            // 请求处理器
            return handler.handle(request, response, this.info);
        }
        // 中间件
        return middleware.handle(request, response, this, this.info);
    }

    public Object then(final Request request, final Response response) {
        return this.handle(request, response);
    }
}
