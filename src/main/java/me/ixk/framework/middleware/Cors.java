package me.ixk.framework.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

public class Cors implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        return next
            .handle(request)
            .header("Access-Control-Allow-Origin", request.getHeader("origin"))
            .header("Access-Control-Allow-Credentials", "true")
            .header("Access-Control-Allow-Headers", "*")
            .header("Access-Control-Allow-Methods", "*");
    }
}
