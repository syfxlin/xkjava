package me.ixk.framework.ioc;

@FunctionalInterface
public interface Callback {
    Object invoke(Application app);
}
