package me.ixk.bootstrap;

import me.ixk.ioc.Application;

public abstract class AbstractBootstrap implements Bootstrap {
    protected Application app;

    public AbstractBootstrap(Application app) {
        this.app = app;
    }
}
