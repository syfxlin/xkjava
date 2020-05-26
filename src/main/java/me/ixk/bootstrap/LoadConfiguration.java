package me.ixk.bootstrap;

import java.lang.reflect.InvocationTargetException;
import me.ixk.ioc.Application;
import me.ixk.utils.Config;

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
