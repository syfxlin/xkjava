package me.ixk.framework.factory;

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject();
}
