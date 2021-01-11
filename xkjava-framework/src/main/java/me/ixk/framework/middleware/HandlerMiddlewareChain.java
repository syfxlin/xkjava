/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import java.util.LinkedList;
import java.util.Queue;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.servlet.InvocableHandlerMethod;

/**
 * 中间件执行链
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:44
 */
public class HandlerMiddlewareChain {

    protected final InvocableHandlerMethod handler;
    protected final Queue<Middleware> middleware;

    public HandlerMiddlewareChain(final InvocableHandlerMethod handler) {
        this.handler = handler;
        this.middleware = new LinkedList<>(handler.getMiddlewares());
    }

    public Object handle(final Request request, final Response response)
        throws Exception {
        final Middleware middleware = this.middleware.poll();
        if (middleware == null) {
            // 请求处理器
            return handler.invokeForRequest(request, response);
        }
        // 中间件
        return middleware.handle(request, response, this);
    }

    public Object then(final Request request, final Response response)
        throws Exception {
        return this.handle(request, response);
    }
}
