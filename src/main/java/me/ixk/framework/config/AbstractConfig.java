package me.ixk.framework.config;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Environment;

public abstract class AbstractConfig implements Config {
    protected Application app;
    protected Environment env;

    public AbstractConfig(Application app) {
        this.app = app;
        this.env = this.app.make(Environment.class);
    }
}
