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
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.route.AnnotationMiddlewareDefinition;
import me.ixk.framework.utils.MergedAnnotation;

public class MiddlewareRegistrar implements AttributeRegistrar {

    @Override
    @SuppressWarnings("unchecked")
    public Object register(
        final XkJava app,
        final String attributeName,
        final AnnotatedElement element,
        final ScopeType scopeType,
        final MergedAnnotation annotation
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
            final String name = annotation.get(RouteMiddleware.class, "name");
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
            final Map<Method, AnnotationMiddlewareDefinition> annotationMiddlewareDefinitions = app.getOrDefaultAttribute(
                attributeName,
                new ConcurrentHashMap<>()
            );
            final me.ixk.framework.annotations.Middleware middlewareAnnotation = annotation.getAnnotation(
                me.ixk.framework.annotations.Middleware.class
            );
            if (middlewareAnnotation != null) {
                try {
                    annotationMiddlewareDefinitions.put(
                        (Method) element,
                        new AnnotationMiddlewareDefinition(
                            middlewareAnnotation.name(),
                            middlewareAnnotation.middleware(),
                            (Method) element
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
