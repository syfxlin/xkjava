package me.ixk.middleware;

@FunctionalInterface
public interface HandlerInterface {
    Object handle(Object request);
}
