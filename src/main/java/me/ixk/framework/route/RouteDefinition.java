package me.ixk.framework.route;

@FunctionalInterface
public interface RouteDefinition {
    void routes(RouteCollector routeCollector);
}
