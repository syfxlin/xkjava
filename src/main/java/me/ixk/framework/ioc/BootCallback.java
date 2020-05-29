package me.ixk.framework.ioc;

@FunctionalInterface
public interface BootCallback {
    Object invoke(Application app);
}
