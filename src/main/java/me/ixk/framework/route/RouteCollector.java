package me.ixk.framework.route;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.ixk.framework.middleware.Handler;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.middleware.Runner;
import org.eclipse.jetty.http.HttpMethod;

public class RouteCollector {
    protected Map<String, Map<String, Handler>> staticRoutes;

    protected Map<String, List<RouteData>> variableRoutes;

    protected RouteParser routeParser;

    protected RouteGenerator routeGenerator;

    protected String routeGroupPrefix = "";

    protected static List<Class<? extends Middleware>> useGroupMiddleware =
        null;

    protected List<Class<? extends Middleware>> middleware = new ArrayList<>();

    public RouteCollector(
        RouteParser routeParser,
        RouteGenerator routeGenerator
    ) {
        this.staticRoutes = new ConcurrentHashMap<>();
        this.variableRoutes = new ConcurrentHashMap<>();
        this.routeParser = routeParser;
        this.routeGenerator = routeGenerator;
    }

    public Handler getHandler(Handler handler) {
        List<Class<? extends Middleware>> middleware = this.middleware;
        this.middleware = new ArrayList<>();
        if (useGroupMiddleware != null) {
            middleware.addAll(useGroupMiddleware);
        }
        if (RouteManager.globalMiddleware != null) {
            middleware.addAll(RouteManager.globalMiddleware);
        }
        return (request, response) -> {
            Runner runner = new Runner(
                handler,
                middleware
                    .stream()
                    .map(
                        ac -> {
                            try {
                                return ac.getConstructor().newInstance();
                            } catch (
                                InstantiationException
                                | IllegalAccessException
                                | InvocationTargetException
                                | NoSuchMethodException e
                            ) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    )
                    .collect(Collectors.toList())
            );
            // TODO: return to response
            return runner.then(request, response);
        };
    }

    public RouteCollector addRoute(
        HttpMethod httpMethod,
        String route,
        Handler handler
    ) {
        return this.addRoute(httpMethod.asString(), route, handler);
    }

    public RouteCollector addRoute(
        HttpMethod[] httpMethods,
        String route,
        Handler handler
    ) {
        String[] methods = new String[httpMethods.length];
        for (int i = 0; i < httpMethods.length; i++) {
            methods[i] = httpMethods[i].asString();
        }
        return this.addRoute(methods, route, handler);
    }

    public RouteCollector addRoute(
        String httpMethod,
        String route,
        Handler handler
    ) {
        return this.addRoute(new String[] { httpMethod }, route, handler);
    }

    public RouteCollector addRoute(
        String[] httpMethods,
        String route,
        Handler handler
    ) {
        route = this.routeGroupPrefix + route;
        RouteData routeData = this.routeParser.parse(route);
        for (String method : httpMethods) {
            if (this.isStaticRoute(routeData)) {
                this.addStaticRoute(
                        method,
                        routeData,
                        this.getHandler(handler)
                    );
            } else {
                this.addVariableRoute(
                        method,
                        routeData,
                        this.getHandler(handler)
                    );
            }
        }
        return this;
    }

    public RouteCollector addGroup(
        String prefix,
        RouteDefinition routeDefinition
    ) {
        useGroupMiddleware = this.middleware;
        this.middleware = new ArrayList<>();
        String prevGroupPrefix = this.routeGroupPrefix;
        this.routeGroupPrefix = prevGroupPrefix + prefix;
        routeDefinition.routes(this);
        this.routeGroupPrefix = prevGroupPrefix;
        useGroupMiddleware = null;
        return this;
    }

    public RouteCollector get(String route, Handler handler) {
        return this.addRoute("GET", route, handler);
    }

    public RouteCollector post(String route, Handler handler) {
        return this.addRoute("POST", route, handler);
    }

    public RouteCollector put(String route, Handler handler) {
        return this.addRoute("PUT", route, handler);
    }

    public RouteCollector delete(String route, Handler handler) {
        return this.addRoute("DELETE", route, handler);
    }

    public RouteCollector patch(String route, Handler handler) {
        return this.addRoute("PATCH", route, handler);
    }

    public RouteCollector head(String route, Handler handler) {
        return this.addRoute("HEAD", route, handler);
    }

    public RouteCollector options(String route, Handler handler) {
        return this.addRoute("OPTIONS", route, handler);
    }

    public RouteCollector match(
        String[] httpMethods,
        String route,
        Handler handler
    ) {
        return this.addRoute(httpMethods, route, handler);
    }

    public RouteCollector match(
        HttpMethod[] httpMethods,
        String route,
        Handler handler
    ) {
        return this.addRoute(httpMethods, route, handler);
    }

    public RouteCollector any(String route, Handler handler) {
        return this.addRoute(
                new String[] {
                    "GET",
                    "POST",
                    "PUT",
                    "DELETE",
                    "PATCH",
                    "HEAD",
                    "OPTIONS",
                },
                route,
                handler
            );
    }

    public RouteCollector prefix(String prefix) {
        this.routeGroupPrefix = prefix;
        return this;
    }

    public RouteCollector group(RouteDefinition routeDefinition) {
        return this.addGroup(this.routeGroupPrefix, routeDefinition);
    }

    public RouteCollector group(
        String prefix,
        RouteDefinition routeDefinition
    ) {
        return this.addGroup(prefix, routeDefinition);
    }

    public RouteCollector redirect(String oldRoute, String newRoute) {
        return this.redirect(oldRoute, newRoute, 301);
    }

    public RouteCollector redirect(
        String oldRoute,
        String newRoute,
        int status
    ) {
        return this.get(
                oldRoute,
                (request, response) -> {
                    try {
                        response.redirect(newRoute, status);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                }
            );
    }

    public RouteCollector middleware(Class<? extends Middleware> middleware) {
        this.middleware.add(middleware);
        return this;
    }

    public RouteCollector middleware(String name) {
        this.middleware.add(RouteManager.routeMiddleware.get(name));
        return this;
    }

    public RouteCollector middleware(String[] names) {
        for (String name : names) {
            this.middleware(name);
        }
        return this;
    }

    public RouteCollector middleware(Class<? extends Middleware>[] middleware) {
        for (Class<? extends Middleware> m : middleware) {
            this.middleware(m);
        }
        return this;
    }

    public RouteCollector view(
        String route,
        String view,
        Map<String, Object> data
    ) {
        return this.get(
                route,
                (request, response) -> {
                    // TODO: view
                    return response;
                }
            );
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
