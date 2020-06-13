package me.ixk.framework.bootstrap;

import me.ixk.framework.ioc.Application;

public abstract class AbstractBootstrap implements Bootstrap {
    protected final Application app;

    public AbstractBootstrap(Application app) {
        this.app = app;
    }
}
