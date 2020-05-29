package me.ixk.framework.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

@FunctionalInterface
public interface Handler {
    void handle(Request request, Response response);
}
