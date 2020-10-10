/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.GlobalMiddleware;
import me.ixk.framework.annotations.RouteMiddleware;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.helpers.Util;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.route.AnnotationMiddlewareDefinition;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergeAnnotation;

public class MiddlewareRegistrar implements AttributeRegistrar {

    @Override
    @SuppressWarnings("unchecked")
    public Object register(
        final XkJava app,
        final String attributeName,
        final AnnotatedElement element,
        final ScopeType scopeType,
        final MergeAnnotation annotation
    ) {
        if (
            annotation.hasAnnotation(GlobalMiddleware.class) &&
            Middleware.class.isAssignableFrom((Class<?>) element)
        ) {
            final List<Class<? extends Middleware>> globalMiddleware = app.getOrDefaultAttribute(
                attributeName,
                new ArrayList<>()
            );
            globalMiddleware.add(
                (Class<? extends me.ixk.framework.middleware.Middleware>) element
            );
            return globalMiddleware;
        }
        if (
            annotation.hasAnnotation(RouteMiddleware.class) &&
            Middleware.class.isAssignableFrom((Class<?>) element)
        ) {
            final Map<String, Class<? extends Middleware>> routeMiddleware = app.getOrDefaultAttribute(
                attributeName,
                new ConcurrentHashMap<>()
            );
            final String name = AnnotationUtils.getAnnotationValue(
                element,
                RouteMiddleware.class,
                "name"
            );
            routeMiddleware.put(
                name,
                (Class<? extends me.ixk.framework.middleware.Middleware>) element
            );
            return routeMiddleware;
        }
        if (
            annotation.hasAnnotation(
                me.ixk.framework.annotations.Middleware.class
            )
        ) {
            final Map<String, AnnotationMiddlewareDefinition> annotationMiddlewareDefinitions = app.getOrDefaultAttribute(
                attributeName,
                new ConcurrentHashMap<>()
            );
            final MergeAnnotation middlewareAnnotation = AnnotationUtils.getAnnotation(
                element,
                me.ixk.framework.annotations.Middleware.class
            );
            if (middlewareAnnotation != null) {
                try {
                    final String handler = Util.routeHandler((Method) element);
                    annotationMiddlewareDefinitions.put(
                        handler,
                        new AnnotationMiddlewareDefinition(
                            middlewareAnnotation.get("name"),
                            middlewareAnnotation.get("middleware"),
                            handler
                        )
                    );
                    return annotationMiddlewareDefinitions;
                } catch (final Exception e) {
                    throw new AnnotationProcessorException(
                        "Middleware annotation process error",
                        e
                    );
                }
            }
        }
        return null;
    }
}
