package me.ixk.framework.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

public class Cors implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        return next
            .handle(request)
            .setHeader(
                "Access-Control-Allow-Origin",
                request.getHeader("origin")
            )
            .setHeader("Access-Control-Allow-Credentials", "true")
            .setHeader("Access-Control-Allow-Headers", "*")
            .setHeader("Access-Control-Allow-Methods", "*");
    }
}
