package me.ixk.framework.providers;

import me.ixk.framework.ioc.Application;

public abstract class AbstractProvider implements Provider {
    protected final Application app;

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
