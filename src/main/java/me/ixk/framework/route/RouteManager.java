package me.ixk.framework.route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.exceptions.RouteCollectorException;
import me.ixk.framework.facades.Config;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.ResponseProcessor;
import me.ixk.framework.middleware.Middleware;

public class RouteManager {
    public static RouteCollector route;

    public static List<Class<? extends Middleware>> globalMiddleware;

    public static Map<String, Class<? extends Middleware>> routeMiddleware;

    public static final List<AnnotationRouteDefinition> annotationRouteDefinitions = new ArrayList<>();

    public static final Map<String, AnnotationMiddlewareDefinition> annotationMiddlewareDefinitions = new ConcurrentHashMap<>();

    protected final RouteDispatcher dispatcher;

    @SuppressWarnings("unchecked")
    public RouteManager() {
        Map<String, Object> middlewareConfig = Config.get(
            "middleware",
            Map.class
        );
        Map<String, Class<? extends RouteDefinition>> routeConfig = Config.get(
            "route",
            Map.class
        );
        globalMiddleware =
            (List<Class<? extends Middleware>>) middlewareConfig.get("global");
        routeMiddleware =
            (Map<String, Class<? extends Middleware>>) middlewareConfig.get(
                "route"
            );
        dispatcher =
            RouteDispatcher.dispatcher(
                routeCollector -> {
                    route = routeCollector;
                    for (Class<? extends RouteDefinition> _class : routeConfig.values()) {
                        try {
                            _class
                                .getConstructor()
                                .newInstance()
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

        // 修改 Request 中的 Path 参数
        Map<String, String> pathParams;
        if ((pathParams = routeResult.getParams()) != null) {
            for (Map.Entry<String, String> entry : pathParams.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        switch (routeResult.getStatus()) {
            case NOT_FOUND:
                throw new HttpException(
                    404,
                    "Not Found",
                    "The URI \"" + request.getHttpURI() + "\" was not found."
                );
            case METHOD_NOT_ALLOWED:
                throw new HttpException(
                    405,
                    "Method Not Allowed",
                    "Method \"" + request.getMethod() + "\" is not allowed."
                );
            case FOUND:
                routeResult.getHandler().handle(request, response);
                break;
        }
        return ResponseProcessor.dispatchResponse(response);
    }
}
