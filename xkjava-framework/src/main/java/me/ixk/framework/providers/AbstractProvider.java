/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.ioc.XkJava;

public abstract class AbstractProvider implements Provider {
    protected final XkJava app;

    protected boolean booted = false;

    public AbstractProvider(XkJava app) {
        this.app = app;
    }

    @Override
    public boolean isBooted() {
        return booted;
    }

    @Override
    public void setBooted(boolean booted) {
        this.booted = booted;
    }

    @Override
    public void boot() {}
}