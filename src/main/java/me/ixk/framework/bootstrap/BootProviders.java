package me.ixk.framework.bootstrap;

import me.ixk.framework.ioc.Application;

public class BootProviders extends AbstractBootstrap {

    public BootProviders(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        this.app.getProviderManager().boot();
    }
}
