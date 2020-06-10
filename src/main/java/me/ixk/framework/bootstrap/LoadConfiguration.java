package me.ixk.framework.bootstrap;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Config;

public class LoadConfiguration extends AbstractBootstrap {

    public LoadConfiguration(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        this.app.instance(Config.class, new Config(this.app), "config");
    }
}
