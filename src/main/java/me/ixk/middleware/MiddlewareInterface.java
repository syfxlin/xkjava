package me.ixk.middleware;

@FunctionalInterface
public interface MiddlewareInterface {
    Object handle(Object request, Runner next);
}
