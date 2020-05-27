package me.ixk.bootstrap;

import java.util.List;
import me.ixk.facades.Config;
import me.ixk.ioc.Application;
import me.ixk.kernel.ProviderManager;

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
