/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.RequestMapping;
import me.ixk.framework.annotations.RequestMethod;
import me.ixk.framework.annotations.Route;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.route.AnnotationRouteDefinition;
import me.ixk.framework.route.RouteDefinition;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RouteRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:54
 */
@Component(name = "routeRegistry")
public class RouteRegistry implements AfterImportBeanRegistry {
    private static final Logger log = LoggerFactory.getLogger(
        RouteRegistry.class
    );

    private final List<Class<? extends RouteDefinition>> routeDefinition = new ArrayList<>();
    private final List<AnnotationRouteDefinition> annotationRouteDefinitions = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void register(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    ) {
        if (
            annotation.hasAnnotation(Route.class) &&
            RouteDefinition.class.isAssignableFrom((Class<?>) element)
        ) {
            this.routeDefinition.add(
                    (Class<? extends RouteDefinition>) element
                );
        }
        if (
            annotation.hasAnnotation(RequestMapping.class) &&
            element instanceof Method
        ) {
            Method method = (Method) element;
            final RequestMapping a = annotation.getAnnotation(
                RequestMapping.class
            );
            if (a == null) {
                return;
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
    }

    public List<Class<? extends RouteDefinition>> getRouteDefinition() {
        return routeDefinition;
    }

    public List<AnnotationRouteDefinition> getAnnotationRouteDefinitions() {
        return annotationRouteDefinitions;
    }
}
