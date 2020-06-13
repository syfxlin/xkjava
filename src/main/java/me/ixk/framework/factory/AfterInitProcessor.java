package me.ixk.framework.factory;

@FunctionalInterface
public interface AfterInitProcessor {
    Object process(Object object, Class<?> returnType);
}
