package me.ixk.framework.factory;

@FunctionalInterface
public interface ObjectBeforeInitProcessor {
    Object before(Object object);
}
