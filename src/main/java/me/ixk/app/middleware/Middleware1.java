package me.ixk.app.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.middleware.Runner;

public class Middleware1 implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        System.out.println("Middleware1");
        return next.handle(request);
    }
}
