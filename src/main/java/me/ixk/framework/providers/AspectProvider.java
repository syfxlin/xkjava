package me.ixk.framework.providers;

import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.ioc.Application;

public class AspectProvider extends AbstractProvider {

    public AspectProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.singleton(
                AspectManager.class,
                (container, args) -> new AspectManager(app),
                "aspect"
            );
    }

    @Override
    public void boot() {
        this.app.make(AspectManager.class);
    }
}