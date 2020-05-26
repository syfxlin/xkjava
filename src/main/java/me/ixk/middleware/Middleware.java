package me.ixk.middleware;

@FunctionalInterface
public interface Middleware {
    Object handle(Object request, Runner next);
}
