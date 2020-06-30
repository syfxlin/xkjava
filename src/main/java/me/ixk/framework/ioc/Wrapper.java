package me.ixk.framework.ioc;

@FunctionalInterface
public interface Wrapper {
    Object getInstance(Container container, With with) throws Throwable;
}
