/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.route.RouteManager;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 10)
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
