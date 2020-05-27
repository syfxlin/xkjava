package me.ixk.bootstrap;

import me.ixk.ioc.Application;

public class BootProviders extends AbstractBootstrap {

    public BootProviders(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        this.app.getProviderManager().boot();
    }
}
