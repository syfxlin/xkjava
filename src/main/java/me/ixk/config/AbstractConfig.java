package me.ixk.config;

import me.ixk.ioc.Application;
import me.ixk.utils.Environment;

public abstract class AbstractConfig implements Config {
    protected Application app;
    protected Environment env;

    public AbstractConfig(Application app) {
        this.app = app;
        this.env = this.app.make(Environment.class);
    }
}
