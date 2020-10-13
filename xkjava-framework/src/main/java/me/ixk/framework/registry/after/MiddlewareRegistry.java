/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.GlobalMiddleware;
import me.ixk.framework.annotations.RouteMiddleware;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.route.AnnotationMiddlewareDefinition;
import me.ixk.framework.utils.MergedAnnotation;

@Component(name = "middlewareRegistry")
public class MiddlewareRegistry implements AfterImportBeanRegistry {
    private final List<Class<? extends Middleware>> globalMiddleware = new ArrayList<>();
    private final Map<String, Class<? extends Middleware>> routeMiddleware = new ConcurrentHashMap<>();
    private final Map<Method, AnnotationMiddlewareDefinition> annotationMiddlewareDefinitions = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void after(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        if (
            annotation.hasAnnotation(GlobalMiddleware.class) &&
            Middleware.class.isAssignableFrom((Class<?>) element)
        ) {
            this.globalMiddleware.add(
                    (Class<? extends me.ixk.framework.middleware.Middleware>) element
                );
        }
        if (
            annotation.hasAnnotation(RouteMiddleware.class) &&
            Middleware.class.isAssignableFrom((Class<?>) element)
        ) {
            final String name = annotation.get(RouteMiddleware.class, "name");
            this.routeMiddleware.put(
                    name,
                    (Class<? extends me.ixk.framework.middleware.Middleware>) element
                );
        }
        if (
            annotation.hasAnnotation(
                me.ixk.framework.annotations.Middleware.class
            )
        ) {
            for (me.ixk.framework.annotations.Middleware middleware : annotation.getAnnotations(
                me.ixk.framework.annotations.Middleware.class
            )) {
                try {
                    this.annotationMiddlewareDefinitions.put(
                            (Method) element,
                            new AnnotationMiddlewareDefinition(
                                middleware.name(),
                                middleware.middleware(),
                                (Method) element
                            )
                        );
                } catch (final Exception e) {
                    throw new AnnotationProcessorException(
                        "Middleware annotation process error",
                        e
                    );
                }
            }
        }
    }

    public List<Class<? extends Middleware>> getGlobalMiddleware() {
        return globalMiddleware;
    }

    public Map<String, Class<? extends Middleware>> getRouteMiddleware() {
        return routeMiddleware;
    }

    public Map<Method, AnnotationMiddlewareDefinition> getAnnotationMiddlewareDefinitions() {
        return annotationMiddlewareDefinitions;
    }
}
