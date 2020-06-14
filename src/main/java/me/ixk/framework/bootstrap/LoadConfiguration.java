package me.ixk.framework.bootstrap;

import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.processor.ConfigurationAnnotationProcessor;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Config;

@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class LoadConfiguration extends AbstractBootstrap {

    public LoadConfiguration(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        ConfigurationAnnotationProcessor processor = new ConfigurationAnnotationProcessor(
            app
        );
        processor.process();
        this.app.instance(Config.class, new Config(this.app), "config");
    }
}
