package me.ixk.framework.bootstrap;

import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Environment;

@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 1)
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
