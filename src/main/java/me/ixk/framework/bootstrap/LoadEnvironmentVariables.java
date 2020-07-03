package me.ixk.framework.bootstrap;

import java.io.IOException;
import java.util.Properties;
import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.exceptions.LoadEnvironmentFileException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.Environment;

@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class LoadEnvironmentVariables extends AbstractBootstrap {

    public LoadEnvironmentVariables(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        Properties property = new Properties();
        try {
            property.load(
                this.getClass().getResourceAsStream("/application.properties")
            );
        } catch (IOException e) {
            throw new LoadEnvironmentFileException(
                "Load environment [application.properties] failed",
                e
            );
        }
        this.app.instance(
                Environment.class,
                new Environment(this.app, property),
                "env"
            );
    }
}
