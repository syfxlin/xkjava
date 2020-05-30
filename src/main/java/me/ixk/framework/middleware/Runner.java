package me.ixk.framework.middleware;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

public class Runner {
    protected Handler handler;

    protected Queue<Middleware> middleware;

    public Runner(Handler handler, List<Middleware> middleware) {
        this(handler, (Queue<Middleware>) new LinkedList<>(middleware));
    }

    public Runner(Handler handler, Queue<Middleware> middleware) {
        this.handler = handler;
        this.middleware = middleware;
    }

    public Object handle(Request request, Response response) {
        Middleware middleware = this.middleware.poll();
        if (middleware == null) {
            // 请求处理器
            return handler.handle(request, response);
        }
        // 中间件
        return middleware.handle(request, response, this);
    }

    public Object then(Request request, Response response) {
        return this.handle(request, response);
    }
}
