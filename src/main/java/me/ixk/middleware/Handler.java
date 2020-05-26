package me.ixk.middleware;

@FunctionalInterface
public interface Handler {
    Object handle(Object request);
}
