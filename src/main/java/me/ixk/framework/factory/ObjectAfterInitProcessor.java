package me.ixk.framework.factory;

@FunctionalInterface
public interface ObjectAfterInitProcessor {
    Object after(Object object);
}
