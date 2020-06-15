package me.ixk.framework.route;

@SuppressWarnings("EmptyMethod")
@FunctionalInterface
public interface RouteDefinition {
    void routes(RouteCollector routeCollector);
}
