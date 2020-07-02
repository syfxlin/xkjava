package me.ixk.app.route;

import me.ixk.framework.annotations.Route;
import me.ixk.framework.route.RouteCollector;
import me.ixk.framework.route.RouteDefinition;

@Route
public class WebRoute implements RouteDefinition {

    @Override
    public void routes(RouteCollector r) {
        System.out.println();
    }
}
