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
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.exception.RouteCollectorException;
import me.ixk.framework.http.HttpMethod;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.registry.after.MiddlewareRegistry;
import me.ixk.framework.registry.after.RouteRegistry;
import me.ixk.framework.servlet.HandlerMethod;
import me.ixk.framework.util.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 路由收集器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:49
 */
@Component(name = "routeCollector")
public class RouteCollector {

    private static final Logger log = LoggerFactory.getLogger(
        RouteCollector.class
    );

    private final XkJava app;
    private final Map<String, Map<String, HandlerMethod>> staticRoutes;
    private final Map<String, List<RouteData>> variableRoutes;

    private final RouteParser routeParser;
    private final RouteGenerator routeGenerator;

    private final MiddlewareRegistry middlewareRegistry;
    private volatile String routeGroupPrefix = "";
    private List<Class<? extends Middleware>> useGroupMiddleware = null;
    private List<Class<? extends Middleware>> middleware = new ArrayList<>();

    public RouteCollector(
        XkJava app,
        RouteParser routeParser,
        RouteGenerator routeGenerator,
        RouteRegistry routeRegistry,
        MiddlewareRegistry middlewareRegistry
    ) {
        this.app = app;
        this.middlewareRegistry = middlewareRegistry;
        this.staticRoutes = new ConcurrentHashMap<>();
        this.variableRoutes = new ConcurrentHashMap<>();
        this.routeParser = routeParser;
        this.routeGenerator = routeGenerator;
        for (Class<? extends RouteDefinition> clazz : routeRegistry.getRouteDefinitions()) {
            try {
                ReflectUtil.newInstance(clazz).routes(this);
            } catch (Exception e) {
                log.error(
                    "Route collector [" + clazz.getSimpleName() + "] error",
                    e
                );
                throw new RouteCollectorException(
                    "Route collector [" + clazz.getSimpleName() + "] error",
                    e
                );
            }
        }
        for (AnnotationRouteDefinition definition : routeRegistry.getAnnotationRouteDefinitions()) {
            this.match(
                    definition.getMethod(),
                    definition.getRoute(),
                    definition.getHandler()
                );
        }
        routeRegistry.getWebSocketDefinitions().forEach(this::get);
    }

    private HandlerMethod getHandler(HandlerMethod handler) {
        this.registerAnnotationMiddleware(handler);
        List<Class<? extends Middleware>> middleware = this.middleware;
        this.middleware = new ArrayList<>();
        if (this.useGroupMiddleware != null) {
            middleware.addAll(this.useGroupMiddleware);
        }
        middleware.addAll(this.middlewareRegistry.getGlobalMiddleware());
        // 重新排序
        AnnotationUtils.sortByOrderAnnotation(middleware);
        handler.setMiddlewares(
            middleware.stream().map(this.app::make).collect(Collectors.toList())
        );
        return handler;
    }

    private void registerAnnotationMiddleware(HandlerMethod handler) {
        final Method method = handler.getMethod();
        if (method == null) {
            return;
        }
        AnnotationMiddlewareDefinition definition =
            this.middlewareRegistry.getAnnotationMiddlewareDefinitions()
                .get(method);
        if (definition == null) {
            return;
        }
        if (definition.isClass()) {
            this.middleware(definition.getMiddleware());
        } else {
            this.middleware(definition.getValue());
        }
    }

    public void addRoute(
        HttpMethod httpMethod,
        String route,
        HandlerMethod handler
    ) {
        this.addRoute(httpMethod.asString(), route, handler);
    }

    public void addRoute(
        HttpMethod[] httpMethods,
        String route,
        HandlerMethod handler
    ) {
        for (HttpMethod httpMethod : httpMethods) {
            this.addRoute(httpMethod.asString(), route, handler);
        }
    }

    public void addRoute(
        String httpMethod,
        String route,
        HandlerMethod handler
    ) {
        this.addRoute(new String[] { httpMethod }, route, handler);
    }

    public void addRoute(
        String[] httpMethods,
        String route,
        HandlerMethod handler
    ) {
        route = this.routeGroupPrefix + route;
        RouteData routeData = this.routeParser.parse(route);
        if (log.isDebugEnabled()) {
            log.debug("Add Route: {}", routeData);
        }
        for (String method : httpMethods) {
            if (routeData.isStatic()) {
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

    public void get(String route, HandlerMethod handler) {
        this.addRoute(HttpMethod.GET, route, handler);
    }

    public void post(String route, HandlerMethod handler) {
        this.addRoute(HttpMethod.POST, route, handler);
    }

    public void put(String route, HandlerMethod handler) {
        this.addRoute(HttpMethod.PUT, route, handler);
    }

    public void delete(String route, HandlerMethod handler) {
        this.addRoute(HttpMethod.DELETE, route, handler);
    }

    public void patch(String route, HandlerMethod handler) {
        this.addRoute(HttpMethod.PATCH, route, handler);
    }

    public void head(String route, HandlerMethod handler) {
        this.addRoute(HttpMethod.HEAD, route, handler);
    }

    public void options(String route, HandlerMethod handler) {
        this.addRoute(HttpMethod.OPTIONS, route, handler);
    }

    public void match(
        String[] httpMethods,
        String route,
        HandlerMethod handler
    ) {
        this.addRoute(httpMethods, route, handler);
    }

    public void match(
        HttpMethod[] httpMethods,
        String route,
        HandlerMethod handler
    ) {
        this.addRoute(httpMethods, route, handler);
    }

    public void any(String route, HandlerMethod handler) {
        this.addRoute(
                new HttpMethod[] {
                    HttpMethod.GET,
                    HttpMethod.POST,
                    HttpMethod.PUT,
                    HttpMethod.DELETE,
                    HttpMethod.PATCH,
                    HttpMethod.HEAD,
                    HttpMethod.OPTIONS,
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
        if (log.isDebugEnabled()) {
            log.debug("Add Middleware: {}", middleware);
        }
        this.middleware.add(middleware);
        return this;
    }

    public RouteCollector middleware(String name) {
        Class<? extends Middleware> middleware =
            this.middlewareRegistry.getRouteMiddleware().get(name);
        if (middleware == null) {
            log.error("Middleware [{}] not register", name);
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

    private void addStaticRoute(
        String httpMethod,
        RouteData routeData,
        HandlerMethod handler
    ) {
        Map<String, HandlerMethod> methodMap =
            this.staticRoutes.getOrDefault(
                    httpMethod,
                    new ConcurrentHashMap<>()
                );
        methodMap.put(routeData.getRegex(), handler);
        this.staticRoutes.put(httpMethod, methodMap);
    }

    private void addVariableRoute(
        String httpMethod,
        RouteData routeData,
        HandlerMethod handler
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

    public Map<String, Map<String, HandlerMethod>> getStaticRoutes() {
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

    public RouteParser getRouteParser() {
        return routeParser;
    }
}
