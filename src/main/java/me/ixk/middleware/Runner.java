package me.ixk.middleware;

import java.util.Queue;

public class Runner {
    protected Handler handler;

    protected Queue<Middleware> middlewares;

    public Runner(Handler handler, Queue<Middleware> middlewares) {
        this.handler = handler;
        this.middlewares = middlewares;
    }

    public Object handle(Object request) {
        Middleware middleware = this.middlewares.poll();
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
