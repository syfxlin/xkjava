package me.ixk.framework.providers;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.route.RouteManager;

public class RouteProvider extends AbstractProvider {

    public RouteProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.singleton(RouteManager.class, RouteManager.class, "route");
    }

    @Override
    public void boot() {
        this.app.make(RouteManager.class);
    }
}
