package me.ixk.framework.bootstrap;

import java.util.List;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.ProviderManager;

public class RegisterProviders extends AbstractBootstrap {

    public RegisterProviders(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void boot() {
        ProviderManager providerManager = new ProviderManager(this.app);
        this.app.setProviderManager(providerManager);
        this.app.instance(
                ProviderManager.class,
                providerManager,
                "providerManager"
            );
        providerManager.registers(Config.get("app.providers", List.class));
    }
}
