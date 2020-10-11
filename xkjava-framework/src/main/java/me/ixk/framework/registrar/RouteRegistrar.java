/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.ixk.framework.annotations.RequestMapping;
import me.ixk.framework.annotations.RequestMethod;
import me.ixk.framework.annotations.Route;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.route.AnnotationRouteDefinition;
import me.ixk.framework.route.RouteDefinition;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;

public class RouteRegistrar implements AttributeRegistrar {

    @Override
    @SuppressWarnings("unchecked")
    public Object register(
        XkJava app,
        String attributeName,
        AnnotatedElement element,
        ScopeType scopeType,
        MergedAnnotation annotation
    ) {
        if (
            annotation.hasAnnotation(Route.class) &&
            RouteDefinition.class.isAssignableFrom((Class<?>) element)
        ) {
            final List<Class<? extends RouteDefinition>> routeDefinition = app.getOrDefaultAttribute(
                attributeName,
                new ArrayList<>()
            );
            routeDefinition.add((Class<? extends RouteDefinition>) element);
            return routeDefinition;
        }
        if (
            annotation.hasAnnotation(RequestMapping.class) &&
            element instanceof Method
        ) {
            final List<AnnotationRouteDefinition> annotationRouteDefinitions = app.getOrDefaultAttribute(
                attributeName,
                new ArrayList<>()
            );
            Method method = (Method) element;
            final RequestMapping a = annotation.getAnnotation(
                RequestMapping.class
            );
            if (a == null) {
                return null;
            }
            final RequestMapping baseMapping = AnnotationUtils
                .getAnnotation(method.getDeclaringClass())
                .getAnnotation(RequestMapping.class);
            for (String basePath : baseMapping == null
                ? new String[] { "" }
                : baseMapping.path()) {
                try {
                    final RequestMethod[] requestMethods = a.method();
                    for (String path : a.path()) {
                        annotationRouteDefinitions.add(
                            new AnnotationRouteDefinition(
                                requestMethods,
                                basePath + path,
                                method
                            )
                        );
                    }
                    return annotationRouteDefinitions;
                } catch (final Exception e) {
                    throw new AnnotationProcessorException(
                        "Route annotation process error",
                        e
                    );
                }
            }
        }
        return null;
    }
}
