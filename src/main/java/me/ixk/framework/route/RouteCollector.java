package me.ixk.framework.route;

import me.ixk.framework.middleware.Handler;
import org.eclipse.jetty.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RouteCollector {
    protected Map<String, Map<String, Handler>> staticRoutes;

    protected Map<String, List<RouteData>> variableRoutes;

    protected RouteParser routeParser;

    protected RouteGenerator routeGenerator;

    protected String routeGroupPrefix = "";

    public RouteCollector(
        RouteParser routeParser,
        RouteGenerator routeGenerator
    ) {
        this.staticRoutes = new ConcurrentHashMap<>();
        this.variableRoutes = new ConcurrentHashMap<>();
        this.routeParser = routeParser;
        this.routeGenerator = routeGenerator;
    }

    public void addRoute(
        HttpMethod httpMethod,
        String route,
        Handler handler
    ) {
        this.addRoute(httpMethod.asString(), route, handler);
    }

    public void addRoute(
        HttpMethod[] httpMethods,
        String route,
        Handler handler
    ) {
        String[] methods = new String[httpMethods.length];
        for (int i = 0; i < httpMethods.length; i++) {
            methods[i] = httpMethods[i].asString();
        }
        this.addRoute(methods, route, handler);
    }

    public void addRoute(
        String httpMethod,
        String route,
        Handler handler
    ) {
        this.addRoute(new String[] { httpMethod }, route, handler);
    }

    public void addRoute(
        String[] httpMethods,
        String route,
        Handler handler
    ) {
        route = this.routeGroupPrefix + route;
        RouteData routeData = this.routeParser.parse(route);
        for (String method : httpMethods) {
            if (this.isStaticRoute(routeData)) {
                this.addStaticRoute(method, routeData, handler);
            } else {
                this.addVariableRoute(method, routeData, handler);
            }
        }
    }

    public void addGroup(String prefix, RouteDefinition routeDefinition) {
        String prevGroupPrefix = this.routeGroupPrefix;
        this.routeGroupPrefix = prevGroupPrefix + prefix;
        routeDefinition.routes(this);
        this.routeGroupPrefix = prevGroupPrefix;
    }

    public void get(String route, Handler handler) {
        this.addRoute("GET", route, handler);
    }

    public void post(String route, Handler handler) {
        this.addRoute("POST", route, handler);
    }

    public void put(String route, Handler handler) {
        this.addRoute("PUT", route, handler);
    }

    public void delete(String route, Handler handler) {
        this.addRoute("DELETE", route, handler);
    }

    public void patch(String route, Handler handler) {
        this.addRoute("PATCH", route, handler);
    }

    public void head(String route, Handler handler) {
        this.addRoute("HEAD", route, handler);
    }

    public void options(String route, Handler handler) {
        this.addRoute("OPTIONS", route, handler);
    }

    protected boolean isStaticRoute(RouteData routeData) {
        return routeData.getVariableNames().isEmpty();
    }

    protected void addStaticRoute(
        String httpMethod,
        RouteData routeData,
        Handler handler
    ) {
        Map<String, Handler> methodMap =
            this.staticRoutes.getOrDefault(
                    httpMethod,
                    new ConcurrentHashMap<>()
                );
        methodMap.put(routeData.getRegex(), handler);
        this.staticRoutes.put(httpMethod, methodMap);
    }

    protected void addVariableRoute(
        String httpMethod,
        RouteData routeData,
        Handler handler
    ) {
        List<RouteData> routeList =
            this.variableRoutes.getOrDefault(httpMethod, new ArrayList<>());
        routeData.setHandler(handler);
        routeList.add(routeData);
        this.variableRoutes.put(httpMethod, routeList);
    }

    public Map<String, MergeRouteData> getMergeVariableRoutes() {
        Map<String, MergeRouteData> map = new ConcurrentHashMap<>();
        for (Map.Entry<String, List<RouteData>> entry : this.variableRoutes.entrySet()) {
            map.put(
                entry.getKey(),
                this.routeGenerator.mergeVariableRoutes(entry.getValue())
            );
        }
        return map;
    }

    public Map<String, Map<String, Handler>> getStaticRoutes() {
        return staticRoutes;
    }

    public Map<String, MergeRouteData> getVariableRoutes() {
        return this.getMergeVariableRoutes();
    }

    public Map<String, List<RouteData>> getOriginVariableRoutes() {
        return this.variableRoutes;
    }

    public RouteGenerator getRouteGenerator() {
        return routeGenerator;
    }
}
