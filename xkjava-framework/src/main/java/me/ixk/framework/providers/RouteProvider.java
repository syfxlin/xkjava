/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.route.RouteCollector;
import me.ixk.framework.route.RouteDispatcher;
import me.ixk.framework.route.RouteGenerator;
import me.ixk.framework.route.RouteManager;
import me.ixk.framework.route.RouteParser;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 10)
public class RouteProvider extends AbstractProvider {

    public RouteProvider(XkJava app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.singleton(RouteParser.class, RouteParser.class, "routeParser");
        this.app.singleton(
                RouteGenerator.class,
                RouteGenerator.class,
                "routeGenerator"
            );
        this.app.singleton(
                RouteCollector.class,
                RouteCollector.class,
                "routeCollector"
            );
        this.app.singleton(
                RouteDispatcher.class,
                RouteDispatcher.class,
                "routeDispatcher"
            );
        this.app.singleton(
                RouteManager.class,
                RouteManager.class,
                "routeManager"
            );
    }

    @Override
    public void boot() {
        this.app.make(RouteManager.class);
    }
}
