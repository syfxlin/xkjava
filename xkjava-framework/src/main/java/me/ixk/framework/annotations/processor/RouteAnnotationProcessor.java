/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RequestMapping;
import me.ixk.framework.annotations.RequestMethod;
import me.ixk.framework.annotations.Route;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.helpers.Util;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.route.AnnotationRouteDefinition;
import me.ixk.framework.route.RouteDefinition;
import me.ixk.framework.utils.AnnotationUtils;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 5)
public class RouteAnnotationProcessor extends AbstractAnnotationProcessor {

    public RouteAnnotationProcessor(final XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        // definition
        this.processDefinitionAnnotation();
        // mapping
        this.processAnnotation(RequestMapping.class);
        // this.processAnnotation(GetMapping.class);
        // this.processAnnotation(PostMapping.class);
        // this.processAnnotation(PutMapping.class);
        // this.processAnnotation(DeleteMapping.class);
        // this.processAnnotation(PatchMapping.class);
    }

    @SuppressWarnings("unchecked")
    public void processDefinitionAnnotation() {
        final List<Class<? extends RouteDefinition>> routeDefinition =
            this.app.getOrDefaultAttribute(
                    "routeDefinition",
                    new ArrayList<>()
                );
        for (final Class<?> _class : this.getTypesAnnotated(Route.class)) {
            if (RouteDefinition.class.isAssignableFrom(_class)) {
                routeDefinition.add((Class<? extends RouteDefinition>) _class);
            }
        }
    }

    public void processAnnotation(
        final Class<? extends Annotation> annotation
    ) {
        final List<AnnotationRouteDefinition> annotationRouteDefinitions =
            this.app.getOrDefaultAttribute(
                    "annotationRouteDefinitions",
                    new ArrayList<>()
                );
        for (final Method method : this.getMethodsAnnotated(annotation)) {
            final RequestMapping a = (RequestMapping) AnnotationUtils.getAnnotation(
                method,
                annotation
            );
            if (a == null) {
                continue;
            }
            final RequestMapping baseMapping = AnnotationUtils.getAnnotation(
                method.getDeclaringClass(),
                RequestMapping.class
            );
            try {
                final RequestMethod[] requestMethods = a.method();
                String requestUrl = baseMapping != null
                    ? baseMapping.path()
                    : "";
                requestUrl += a.path();
                annotationRouteDefinitions.add(
                    new AnnotationRouteDefinition(
                        requestMethods,
                        requestUrl,
                        Util.routeHandler(method)
                    )
                );
            } catch (final Exception e) {
                throw new AnnotationProcessorException(
                    "Route annotation process error",
                    e
                );
            }
        }
    }
}
