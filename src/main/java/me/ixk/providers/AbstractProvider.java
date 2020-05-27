package me.ixk.providers;

import me.ixk.ioc.Application;

public abstract class AbstractProvider implements Provider {
    protected Application app;

    protected boolean booted = false;

    public AbstractProvider(Application app) {
        this.app = app;
    }

    public boolean isBooted() {
        return booted;
    }

    public void setBooted(boolean booted) {
        this.booted = booted;
    }

    @Override
    public void boot() {}
}
