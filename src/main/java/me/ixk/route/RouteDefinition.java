package me.ixk.route;

@FunctionalInterface
public interface RouteDefinition {
    void invoke(RouteCollector routeCollector);
}
