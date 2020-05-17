package me.ixk.middleware;

import java.util.Queue;

public class Runner {
    protected HandlerInterface handler;

    protected Queue<MiddlewareInterface> middlewares;

    public Runner(HandlerInterface handler, Queue<MiddlewareInterface> middlewares) {
        this.handler = handler;
        this.middlewares = middlewares;
    }

    public Object handle(Object request) {
        MiddlewareInterface middleware = this.middlewares.poll();
        if (middleware == null) {
            // 请求处理器
            return handler.handle(request);
        }
        return middleware.handle(request, this);
    }

    public Object then(Object request) {
        return this.handle(request);
    }
}
