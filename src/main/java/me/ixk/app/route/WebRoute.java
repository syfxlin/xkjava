package me.ixk.app.route;

import me.ixk.app.middleware.Handler1;
import me.ixk.framework.route.RouteCollector;
import me.ixk.framework.route.RouteDefinition;

public class WebRoute implements RouteDefinition {

    @Override
    public void routes(RouteCollector routeCollector) {
        routeCollector.get("/user", new Handler1());
    }
}
