package me.ixk.framework.bootstrap;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Config;

import java.lang.reflect.InvocationTargetException;

public class LoadConfiguration extends AbstractBootstrap {

    public LoadConfiguration(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        try {
            this.app.instance(Config.class, new Config(this.app), "config");
        } catch (
            NoSuchMethodException
            | IllegalAccessException
            | InvocationTargetException
            | InstantiationException e
        ) {
            e.printStackTrace();
        }
    }
}
