/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.GlobalMiddleware;
import me.ixk.framework.annotations.Middleware;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RouteMiddleware;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.helpers.Util;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.route.AnnotationMiddlewareDefinition;
import me.ixk.framework.utils.AnnotationUtils;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 6)
public class MiddlewareAnnotationProcessor extends AbstractAnnotationProcessor {

    public MiddlewareAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process() {
        List<Class<? extends me.ixk.framework.middleware.Middleware>> globalMiddleware =
            this.app.getOrDefaultAttribute(
                    "globalMiddleware",
                    new ArrayList<>()
                );
        Map<String, Class<? extends me.ixk.framework.middleware.Middleware>> routeMiddleware =
            this.app.getOrDefaultAttribute(
                    "routeMiddleware",
                    new ConcurrentHashMap<>()
                );
        Map<String, AnnotationMiddlewareDefinition> annotationMiddlewareDefinitions =
            this.app.getOrDefaultAttribute(
                    "annotationMiddlewareDefinitions",
                    new ConcurrentHashMap<>()
                );
        // define
        for (Class<?> clazz : this.getTypesAnnotated(GlobalMiddleware.class)) {
            if (
                me.ixk.framework.middleware.Middleware.class.isAssignableFrom(
                        clazz
                    )
            ) {
                globalMiddleware.add(
                    (Class<? extends me.ixk.framework.middleware.Middleware>) clazz
                );
            }
        }
        for (Class<?> clazz : this.getTypesAnnotated(RouteMiddleware.class)) {
            if (
                me.ixk.framework.middleware.Middleware.class.isAssignableFrom(
                        clazz
                    )
            ) {
                String name = AnnotationUtils.getAnnotationValue(
                    clazz,
                    RouteMiddleware.class,
                    "name"
                );
                routeMiddleware.put(
                    name,
                    (Class<? extends me.ixk.framework.middleware.Middleware>) clazz
                );
            }
        }
        // use
        for (Method method : this.getMethodsAnnotated(Middleware.class)) {
            Middleware annotation = AnnotationUtils.getAnnotation(
                method,
                Middleware.class
            );
            if (annotation == null) {
                continue;
            }
            try {
                String handler = Util.routeHandler(method);
                annotationMiddlewareDefinitions.put(
                    handler,
                    new AnnotationMiddlewareDefinition(
                        annotation.name(),
                        annotation.middleware(),
                        handler
                    )
                );
            } catch (Exception e) {
                throw new AnnotationProcessorException(
                    "Middleware annotation process error",
                    e
                );
            }
        }
    }
}
