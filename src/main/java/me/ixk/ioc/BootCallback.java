package me.ixk.ioc;

@FunctionalInterface
public interface BootCallback {
    Object invoke(Application app);
}
