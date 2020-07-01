package me.ixk.framework.ioc;

@FunctionalInterface
public interface MethodInjector {
    Object inject(Binding binding, Object instance, With with);
}
