package me.ixk.framework.ioc;

@FunctionalInterface
public interface PropertyInjector {
    Object inject(Container container, Object instance, With with);
}
