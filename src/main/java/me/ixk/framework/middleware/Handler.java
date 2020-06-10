package me.ixk.framework.middleware;

import me.ixk.framework.http.Request;

@FunctionalInterface
public interface Handler {
    Object handle(Request request);
}
