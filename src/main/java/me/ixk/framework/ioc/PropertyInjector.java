package me.ixk.framework.ioc;

@FunctionalInterface
public interface PropertyInjector {
    Object inject(Binding binding, Object instance, With with);
}
