package me.ixk.framework.route;

import me.ixk.framework.exceptions.RouteCollectorException;
import me.ixk.framework.facades.Resp;
import me.ixk.framework.facades.View;
import me.ixk.framework.kernel.ControllerHandler;
import me.ixk.framework.middleware.Handler;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.middleware.Runner;
import me.ixk.framework.utils.Helper;
import org.eclipse.jetty.http.HttpMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RouteCollector {
    protected final Map<String, Map<String, RouteHandler>> staticRoutes;

    protected final Map<String, List<RouteData>> variableRoutes;

    protected final RouteParser routeParser;

    protected final RouteGenerator routeGenerator;

    protected String routeGroupPrefix = "";

    protected List<Class<? extends Middleware>> useGroupMiddleware = null;

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

    protected RouteHandler getHandler(Handler handler) {
        List<Class<? extends Middleware>> middleware = this.middleware;
        this.middleware = new ArrayList<>();
        if (this.useGroupMiddleware != null) {
            middleware.addAll(this.useGroupMiddleware);
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
                                throw new RouteCollectorException(
                                    "Instantiating middleware failed",
                                    e
                                );
                            }
                        }
                    )
                    .collect(Collectors.toList())
            );
            return runner.then(request, response);
        };
    }

    protected void registerAnnotationMiddleware(String handler) {
        AnnotationMiddlewareDefinition definition = RouteManager.annotationMiddlewareDefinitions.get(
            handler
        );
        if (definition == null) {
            return;
        }
        if (definition.isClass()) {
            this.middleware(definition.getMiddleware());
        } else {
            this.middleware(definition.getValue());
        }
    }

    public void addRoute(HttpMethod httpMethod, String route, Handler handler) {
        this.addRoute(httpMethod.asString(), route, handler);
    }

    public void addRoute(
        HttpMethod[] httpMethods,
        String route,
        Handler handler
    ) {
        for (HttpMethod httpMethod : httpMethods) {
            this.addRoute(httpMethod.asString(), route, handler);
        }
    }

    public void addRoute(String httpMethod, String route, Handler handler) {
        this.addRoute(new String[] { httpMethod }, route, handler);
    }

    public void addRoute(String[] httpMethods, String route, Handler handler) {
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
    }

    public void addRoute(HttpMethod httpMethod, String route, String handler) {
        this.addRoute(httpMethod.asString(), route, handler);
    }

    public void addRoute(
        HttpMethod[] httpMethod,
        String route,
        String handler
    ) {
        for (HttpMethod method : httpMethod) {
            this.addRoute(method.asString(), route, handler);
        }
    }

    public void addRoute(String httpMethod, String route, String handler) {
        this.addRoute(new String[] { httpMethod }, route, handler);
    }

    public void addRoute(String[] httpMethod, String route, String handler) {
        this.registerAnnotationMiddleware(handler);
        this.addRoute(
                httpMethod,
                route,
                new ControllerHandler(Helper.routeHandler(handler))
            );
    }

    public RouteCollector addGroup(
        String prefix,
        RouteDefinition routeDefinition
    ) {
        this.useGroupMiddleware = this.middleware;
        this.middleware = new ArrayList<>();
        String prevGroupPrefix = this.routeGroupPrefix;
        this.routeGroupPrefix = prevGroupPrefix + prefix;
        routeDefinition.routes(this);
        this.routeGroupPrefix = prevGroupPrefix;
        this.useGroupMiddleware = null;
        return this;
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

    public void match(String[] httpMethods, String route, Handler handler) {
        this.addRoute(httpMethods, route, handler);
    }

    public void match(HttpMethod[] httpMethods, String route, Handler handler) {
        this.addRoute(httpMethods, route, handler);
    }

    public void any(String route, Handler handler) {
        this.addRoute(
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

    public void get(String route, String handler) {
        this.addRoute("GET", route, handler);
    }

    public void post(String route, String handler) {
        this.addRoute("POST", route, handler);
    }

    public void put(String route, String handler) {
        this.addRoute("PUT", route, handler);
    }

    public void delete(String route, String handler) {
        this.addRoute("DELETE", route, handler);
    }

    public void patch(String route, String handler) {
        this.addRoute("PATCH", route, handler);
    }

    public void head(String route, String handler) {
        this.addRoute("HEAD", route, handler);
    }

    public void options(String route, String handler) {
        this.addRoute("OPTIONS", route, handler);
    }

    public void match(String[] httpMethods, String route, String handler) {
        this.addRoute(httpMethods, route, handler);
    }

    public void match(HttpMethod[] httpMethods, String route, String handler) {
        this.addRoute(httpMethods, route, handler);
    }

    public void any(String route, String handler) {
        this.addRoute(
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

    public void redirect(String oldRoute, String newRoute) {
        this.redirect(oldRoute, newRoute, 301);
    }

    public void redirect(String oldRoute, String newRoute, int status) {
        this.get(oldRoute, request -> Resp.redirect(newRoute, status));
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

    public void view(String route, String view, Map<String, Object> data) {
        this.get(route, request -> View.make(view, data));
    }

    protected boolean isStaticRoute(RouteData routeData) {
        return routeData.getVariableNames().isEmpty();
    }

    protected void addStaticRoute(
        String httpMethod,
        RouteData routeData,
        RouteHandler handler
    ) {
        Map<String, RouteHandler> methodMap =
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
        RouteHandler handler
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

    public Map<String, Map<String, RouteHandler>> getStaticRoutes() {
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
