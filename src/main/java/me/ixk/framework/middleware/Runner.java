package me.ixk.framework.middleware;

import java.util.Queue;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

public class Runner {
    protected Handler handler;

    protected Queue<Middleware> middleware;

    public Runner(Handler handler, Queue<Middleware> middleware) {
        this.handler = handler;
        this.middleware = middleware;
    }

    public void handle(Request request, Response response) {
        Middleware middleware = this.middleware.poll();
        if (middleware == null) {
            // 请求处理器
            handler.handle(request, response);
        }
        if (middleware != null) {
            // 中间件
            middleware.handle(request, response, this);
        }
    }

    public Response then(Request request, Response response) {
        this.handle(request, response);
        return response;
    }
}
