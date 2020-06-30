package me.ixk.framework.ioc;

@FunctionalInterface
public interface MethodInjector {
    Object inject(Container container, Object instance, With with);
}
