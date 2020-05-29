package me.ixk.framework.providers;

public interface Provider {
    void register();
    void boot();
    boolean isBooted();
    void setBooted(boolean booted);
}
