/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.annotation.web.RequestMapping;
import me.ixk.framework.annotation.web.Route;
import me.ixk.framework.annotation.web.WebSocket;
import me.ixk.framework.exception.AnnotationProcessorException;
import me.ixk.framework.http.HttpMethod;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.route.AnnotationRouteDefinition;
import me.ixk.framework.route.RouteDefinition;
import me.ixk.framework.servlet.HandlerMethod;
import me.ixk.framework.servlet.WebSocketHandlerMethod;
import me.ixk.framework.util.MergedAnnotation;
import me.ixk.framework.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RouteRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:54
 */
@Component(name = "routeRegistry")
public class RouteRegistry implements AfterBeanRegistry {

    private static final Logger log = LoggerFactory.getLogger(
        RouteRegistry.class
    );

    private final List<Class<? extends RouteDefinition>> routeDefinitions = new ArrayList<>();
    private final List<AnnotationRouteDefinition> annotationRouteDefinitions = new ArrayList<>();
    private final Map<String, HandlerMethod> webSocketDefinitions = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void register(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        if (
            annotation.hasAnnotation(Route.class) &&
            RouteDefinition.class.isAssignableFrom((Class<?>) element)
        ) {
            this.routeDefinitions.add(
                    (Class<? extends RouteDefinition>) element
                );
        }
        if (
            annotation.hasAnnotation(RequestMapping.class) &&
            element instanceof Method
        ) {
            final Method method = (Method) element;
            final RequestMapping a = annotation.getAnnotation(
                RequestMapping.class
            );
            if (a == null) {
                return;
            }
            final RequestMapping baseMapping = MergedAnnotation
                .from(method.getDeclaringClass())
                .getAnnotation(RequestMapping.class);
            for (final String basePath : baseMapping == null
                ? new String[] { "" }
                : baseMapping.path()) {
                try {
                    final HttpMethod[] requestMethods = a.method();
                    for (final String path : a.path()) {
                        this.annotationRouteDefinitions.add(
                                new AnnotationRouteDefinition(
                                    requestMethods,
                                    basePath + path,
                                    method
                                )
                            );
                    }
                } catch (final Exception e) {
                    log.error("Route annotation process error", e);
                    throw new AnnotationProcessorException(
                        "Route annotation process error",
                        e
                    );
                }
            }
        }
        if (
            annotation.hasAnnotation(WebSocket.class) &&
            WebSocketHandler.class.isAssignableFrom((Class<?>) element)
        ) {
            final Class<? extends WebSocketHandler> clazz = (Class<? extends WebSocketHandler>) element;
            final WebSocket webSocket = annotation.getAnnotation(
                WebSocket.class
            );
            for (final String path : webSocket.path()) {
                this.webSocketDefinitions.put(
                        path,
                        new WebSocketHandlerMethod(clazz)
                    );
            }
        }
    }

    public List<Class<? extends RouteDefinition>> getRouteDefinitions() {
        return routeDefinitions;
    }

    public List<AnnotationRouteDefinition> getAnnotationRouteDefinitions() {
        return annotationRouteDefinitions;
    }

    public Map<String, HandlerMethod> getWebSocketDefinitions() {
        return webSocketDefinitions;
    }
}
