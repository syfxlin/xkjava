package me.ixk.framework.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

@FunctionalInterface
public interface Middleware {
    void handle(Request request, Response response, Runner next);
}
