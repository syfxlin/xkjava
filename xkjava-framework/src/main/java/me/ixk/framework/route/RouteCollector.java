/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.exceptions.RouteCollectorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.utils.AnnotationUtils;
import org.eclipse.jetty.http.HttpMethod;

@Component(name = "routeCollector")
public class RouteCollector {
    protected final XkJava app;

    protected final Map<String, Map<String, RouteHandler>> staticRoutes;

    protected final Map<String, List<RouteData>> variableRoutes;

    protected final RouteParser routeParser;

    protected final RouteGenerator routeGenerator;

    protected String routeGroupPrefix = "";

    protected List<Class<? extends Middleware>> useGroupMiddleware = null;

    protected List<Class<? extends Middleware>> middleware = new ArrayList<>();

    protected final List<Class<? extends me.ixk.framework.middleware.Middleware>> globalMiddleware;
    protected final Map<String, Class<? extends me.ixk.framework.middleware.Middleware>> routeMiddleware;
    protected final Map<Method, AnnotationMiddlewareDefinition> annotationMiddlewareDefinitions;

    public RouteCollector(
        XkJava app,
        RouteParser routeParser,
        RouteGenerator routeGenerator
    ) {
        this.app = app;
        this.staticRoutes = new ConcurrentHashMap<>();
        this.variableRoutes = new ConcurrentHashMap<>();
        this.routeParser = routeParser;
        this.routeGenerator = routeGenerator;
        this.globalMiddleware =
            this.app.getOrDefaultAttribute(
                    "globalMiddleware",
                    new ArrayList<>()
                );
        this.routeMiddleware =
            this.app.getOrDefaultAttribute(
                    "routeMiddleware",
                    new ConcurrentHashMap<>()
                );
        this.annotationMiddlewareDefinitions =
            this.app.getOrDefaultAttribute(
                    "annotationMiddlewareDefinitions",
                    new ConcurrentHashMap<>()
                );
        for (Class<? extends RouteDefinition> clazz : this.app.getOrDefaultAttribute(
                "routeDefinition",
                new ArrayList<Class<? extends RouteDefinition>>()
            )) {
            try {
                ReflectUtil.newInstance(clazz).routes(this);
            } catch (Exception e) {
                throw new RouteCollectorException(
                    "Route collector [" + clazz.getSimpleName() + "] error",
                    e
                );
            }
        }
        for (AnnotationRouteDefinition definition : this.app.getOrDefaultAttribute(
                "annotationRouteDefinitions",
                new ArrayList<AnnotationRouteDefinition>()
            )) {
            this.match(
                    definition.getMethod(),
                    definition.getRoute(),
                    definition.getHandler()
                );
        }
    }

    protected RouteHandler getHandler(Method handler) {
        this.registerAnnotationMiddleware(handler);
        List<Class<? extends Middleware>> middleware = this.middleware;
        this.middleware = new ArrayList<>();
        if (this.useGroupMiddleware != null) {
            middleware.addAll(this.useGroupMiddleware);
        }
        if (this.globalMiddleware != null) {
            middleware.addAll(this.globalMiddleware);
        }
        // 重新排序
        AnnotationUtils.sortByOrderAnnotation(middleware);
        return new RouteHandler(
            handler,
            middleware.stream().map(this.app::make).collect(Collectors.toList())
        );
    }

    protected void registerAnnotationMiddleware(Method handler) {
        AnnotationMiddlewareDefinition definition =
            this.annotationMiddlewareDefinitions.get(handler);
        if (definition == null) {
            return;
        }
        if (definition.isClass()) {
            this.middleware(definition.getMiddleware());
        } else {
            this.middleware(definition.getValue());
        }
    }

    public void addRoute(HttpMethod httpMethod, String route, Method handler) {
        this.addRoute(httpMethod.asString(), route, handler);
    }

    public void addRoute(
        HttpMethod[] httpMethods,
        String route,
        Method handler
    ) {
        for (HttpMethod httpMethod : httpMethods) {
            this.addRoute(httpMethod.asString(), route, handler);
        }
    }

    public void addRoute(String httpMethod, String route, Method handler) {
        this.addRoute(new String[] { httpMethod }, route, handler);
    }

    public void addRoute(String[] httpMethods, String route, Method handler) {
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

    public void get(String route, Method handler) {
        this.addRoute("GET", route, handler);
    }

    public void post(String route, Method handler) {
        this.addRoute("POST", route, handler);
    }

    public void put(String route, Method handler) {
        this.addRoute("PUT", route, handler);
    }

    public void delete(String route, Method handler) {
        this.addRoute("DELETE", route, handler);
    }

    public void patch(String route, Method handler) {
        this.addRoute("PATCH", route, handler);
    }

    public void head(String route, Method handler) {
        this.addRoute("HEAD", route, handler);
    }

    public void options(String route, Method handler) {
        this.addRoute("OPTIONS", route, handler);
    }

    public void match(String[] httpMethods, String route, Method handler) {
        this.addRoute(httpMethods, route, handler);
    }

    public void match(HttpMethod[] httpMethods, String route, Method handler) {
        this.addRoute(httpMethods, route, handler);
    }

    public void any(String route, Method handler) {
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

    public RouteCollector middleware(Class<? extends Middleware> middleware) {
        this.middleware.add(middleware);
        return this;
    }

    public RouteCollector middleware(String name) {
        Class<? extends Middleware> middleware = this.routeMiddleware.get(name);
        if (middleware == null) {
            throw new RouteCollectorException(
                "Middleware [" + name + "] not register"
            );
        }
        this.middleware.add(middleware);
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
