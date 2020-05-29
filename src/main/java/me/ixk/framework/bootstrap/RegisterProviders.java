package me.ixk.framework.bootstrap;

import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.ProviderManager;

import java.util.List;

public class RegisterProviders extends AbstractBootstrap {

    public RegisterProviders(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void boot() {
        ProviderManager providerManager = new ProviderManager(this.app);
        this.app.setProviderManager(providerManager);
        providerManager.registers(Config.get("app.providers", List.class));
        this.app.instance(
                ProviderManager.class,
                providerManager,
                "providerManager"
            );
    }
}
