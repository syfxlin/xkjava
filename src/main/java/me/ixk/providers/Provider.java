package me.ixk.providers;

public interface Provider {
    void register();
    void boot();
    boolean isBooted();
    void setBooted(boolean booted);
}
