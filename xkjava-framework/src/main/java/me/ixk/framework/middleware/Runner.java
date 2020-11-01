/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.ResponseProcessor;

/**
 * 中间件执行链
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:44
 */
public class Runner {
    protected final Handler handler;
    protected final Queue<Middleware> middleware;
    protected Response response;

    public Runner(Handler handler, List<Middleware> middleware) {
        this(handler, (Queue<Middleware>) new LinkedList<>(middleware));
    }

    public Runner(Handler handler, Queue<Middleware> middleware) {
        this.handler = handler;
        this.middleware = middleware;
    }

    public Response handle(Request request) {
        Middleware middleware = this.middleware.poll();
        if (middleware == null) {
            // 请求处理器
            return ResponseProcessor.toResponse(
                request,
                this.response,
                handler.handle(request, this.response)
            );
        }
        // 中间件
        return middleware.handle(request, this);
    }

    public Response then(Request request, Response response) {
        this.response = response;
        return this.handle(request);
    }
}
