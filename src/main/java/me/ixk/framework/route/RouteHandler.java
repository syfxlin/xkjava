package me.ixk.framework.route;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

@FunctionalInterface
public interface RouteHandler {
    Response handle(Request request, Response response);
}
