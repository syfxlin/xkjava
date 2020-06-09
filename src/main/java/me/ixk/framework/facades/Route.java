package me.ixk.framework.facades;

import java.util.List;
import java.util.Map;
import me.ixk.framework.middleware.Handler;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.route.*;
import org.eclipse.jetty.http.HttpMethod;

public class Route extends AbstractFacade {

    protected static RouteCollector make() {
        return RouteManager.route;
    }

    public static void addRoute(
        HttpMethod httpMethod,
        String route,
        Handler handler
    ) {
        make().addRoute(httpMethod, route, handler);
    }

    public static void addRoute(
        HttpMethod[] httpMethods,
        String route,
        Handler handler
    ) {
        make().addRoute(httpMethods, route, handler);
    }

    public static void addRoute(
        String httpMethod,
        String route,
        Handler handler
    ) {
        make().addRoute(httpMethod, route, handler);
    }

    public static void addRoute(
        String[] httpMethods,
        String route,
        Handler handler
    ) {
        make().addRoute(httpMethods, route, handler);
    }

    public static void addRoute(
        HttpMethod httpMethod,
        String route,
        String handler
    ) {
        make().addRoute(httpMethod, route, handler);
    }

    public static void addRoute(
        HttpMethod[] httpMethod,
        String route,
        String handler
    ) {
        make().addRoute(httpMethod, route, handler);
    }

    public static void addRoute(
        String httpMethod,
        String route,
        String handler
    ) {
        make().addRoute(httpMethod, route, handler);
    }

    public static void addRoute(
        String[] httpMethod,
        String route,
        String handler
    ) {
        make().addRoute(httpMethod, route, handler);
    }

    public static RouteCollector addGroup(
        String prefix,
        RouteDefinition routeDefinition
    ) {
        return make().addGroup(prefix, routeDefinition);
    }

    public static void get(String route, Handler handler) {
        make().get(route, handler);
    }

    public static void post(String route, Handler handler) {
        make().post(route, handler);
    }

    public static void put(String route, Handler handler) {
        make().put(route, handler);
    }

    public static void delete(String route, Handler handler) {
        make().delete(route, handler);
    }

    public static void patch(String route, Handler handler) {
        make().patch(route, handler);
    }

    public static void head(String route, Handler handler) {
        make().head(route, handler);
    }

    public static void options(String route, Handler handler) {
        make().options(route, handler);
    }

    public static void match(
        String[] httpMethods,
        String route,
        Handler handler
    ) {
        make().match(httpMethods, route, handler);
    }

    public static void match(
        HttpMethod[] httpMethods,
        String route,
        Handler handler
    ) {
        make().match(httpMethods, route, handler);
    }

    public static void any(String route, Handler handler) {
        make().any(route, handler);
    }

    public static void get(String route, String handler) {
        make().get(route, handler);
    }

    public static void post(String route, String handler) {
        make().post(route, handler);
    }

    public static void put(String route, String handler) {
        make().put(route, handler);
    }

    public static void delete(String route, String handler) {
        make().delete(route, handler);
    }

    public static void patch(String route, String handler) {
        make().patch(route, handler);
    }

    public static void head(String route, String handler) {
        make().head(route, handler);
    }

    public static void options(String route, String handler) {
        make().options(route, handler);
    }

    public static void match(
        String[] httpMethods,
        String route,
        String handler
    ) {
        make().match(httpMethods, route, handler);
    }

    public static void match(
        HttpMethod[] httpMethods,
        String route,
        String handler
    ) {
        make().match(httpMethods, route, handler);
    }

    public static void any(String route, String handler) {
        make().any(route, handler);
    }

    public static RouteCollector prefix(String prefix) {
        return make().prefix(prefix);
    }

    public static RouteCollector group(RouteDefinition routeDefinition) {
        return make().group(routeDefinition);
    }

    public static RouteCollector group(
        String prefix,
        RouteDefinition routeDefinition
    ) {
        return make().group(prefix, routeDefinition);
    }

    public static void redirect(String oldRoute, String newRoute) {
        make().redirect(oldRoute, newRoute);
    }

    public static void redirect(String oldRoute, String newRoute, int status) {
        make().redirect(oldRoute, newRoute, status);
    }

    public static RouteCollector middleware(
        Class<? extends Middleware> middleware
    ) {
        return make().middleware(middleware);
    }

    public static RouteCollector middleware(String name) {
        return make().middleware(name);
    }

    public static RouteCollector middleware(String[] names) {
        return make().middleware(names);
    }

    public static RouteCollector middleware(
        Class<? extends Middleware>[] middleware
    ) {
        return make().middleware(middleware);
    }

    public static void view(
        String route,
        String view,
        Map<String, Object> data
    ) {
        make().view(route, view, data);
    }

    public static Map<String, MergeRouteData> getMergeVariableRoutes() {
        return make().getMergeVariableRoutes();
    }

    public static Map<String, Map<String, Handler>> getStaticRoutes() {
        return make().getStaticRoutes();
    }

    public static Map<String, MergeRouteData> getVariableRoutes() {
        return make().getVariableRoutes();
    }

    public static Map<String, List<RouteData>> getOriginVariableRoutes() {
        return make().getOriginVariableRoutes();
    }

    public static RouteGenerator getRouteGenerator() {
        return make().getRouteGenerator();
    }
}
