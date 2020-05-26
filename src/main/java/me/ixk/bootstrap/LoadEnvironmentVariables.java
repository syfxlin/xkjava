package me.ixk.bootstrap;

import me.ixk.ioc.Application;
import me.ixk.utils.Environment;

public class LoadEnvironmentVariables extends AbstractBootstrap {

    public LoadEnvironmentVariables(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        this.app.instance(
                Environment.class,
                new Environment("/application.properties"),
                "env"
            );
    }
}
