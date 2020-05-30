package me.ixk.app.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.middleware.Runner;

public class Middleware1 implements Middleware {

    @Override
    public Object handle(Request request, Response response, Runner next) {
        System.out.println("Middleware1");
        return next.handle(request, response);
    }
}
