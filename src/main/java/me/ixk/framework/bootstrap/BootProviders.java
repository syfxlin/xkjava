package me.ixk.framework.bootstrap;

import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.Application;

@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 6)
public class BootProviders extends AbstractBootstrap {

    public BootProviders(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        this.app.getProviderManager().boot();
    }
}
