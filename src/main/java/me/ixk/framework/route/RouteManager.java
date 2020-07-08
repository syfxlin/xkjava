/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import cn.hutool.core.util.ReflectUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.exceptions.RouteCollectorException;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.ResponseProcessor;
import me.ixk.framework.middleware.Middleware;

public class RouteManager {
    public static RouteCollector route;

    public static final List<Class<? extends RouteDefinition>> routeDefinition = new ArrayList<>();

    public static final List<Class<? extends Middleware>> globalMiddleware = new ArrayList<>(
        10
    );

    public static final Map<String, Class<? extends Middleware>> routeMiddleware = new ConcurrentHashMap<>(
        10
    );

    public static final List<AnnotationRouteDefinition> annotationRouteDefinitions = new ArrayList<>();

    public static final Map<String, AnnotationMiddlewareDefinition> annotationMiddlewareDefinitions = new ConcurrentHashMap<>();

    protected final RouteDispatcher dispatcher;

    public RouteManager() {
        dispatcher =
            RouteDispatcher.dispatcher(
                routeCollector -> {
                    route = routeCollector;
                    for (Class<? extends RouteDefinition> _class : routeDefinition) {
                        try {
                            ReflectUtil
                                .newInstance(_class)
                                .routes(routeCollector);
                        } catch (Exception e) {
                            throw new RouteCollectorException(
                                "Route collector [" +
                                _class.getSimpleName() +
                                "] error",
                                e
                            );
                        }
                    }
                    for (AnnotationRouteDefinition definition : annotationRouteDefinitions) {
                        routeCollector.match(
                            definition.getMethod(),
                            definition.getRoute(),
                            definition.getHandler()
                        );
                    }
                }
            );
    }

    public Response dispatch(Request request, Response response) {
        return this.handleRequest(this.dispatcher, request, response);
    }

    public Response handleRequest(
        RouteDispatcher dispatcher,
        Request request,
        Response response
    ) {
        RouteResult routeResult = dispatcher.dispatch(
            request.getMethod(),
            request.getHttpURI().getPath()
        );

        // 将 Route 信息设置到 Request
        request.setRoute(routeResult);

        switch (routeResult.getStatus()) {
            case NOT_FOUND:
                throw new HttpException(
                    HttpStatus.NOT_FOUND,
                    "The URI \"" + request.getHttpURI() + "\" was not found."
                );
            case METHOD_NOT_ALLOWED:
                throw new HttpException(
                    HttpStatus.METHOD_NOT_ALLOWED,
                    "Method \"" + request.getMethod() + "\" is not allowed."
                );
            case FOUND:
                routeResult.getHandler().handle(request, response);
                break;
        }
        return ResponseProcessor.dispatchResponse(response);
    }
}
