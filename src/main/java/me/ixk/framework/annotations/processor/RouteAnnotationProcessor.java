/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.*;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.helpers.Util;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.route.AnnotationRouteDefinition;
import me.ixk.framework.route.RouteDefinition;
import me.ixk.framework.route.RouteManager;
import me.ixk.framework.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 5)
public class RouteAnnotationProcessor extends AbstractAnnotationProcessor {

    public RouteAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        // definition
        this.processDefinitionAnnotation();
        // mapping
        this.processAnnotation(RequestMapping.class);
        this.processAnnotation(GetMapping.class);
        this.processAnnotation(PostMapping.class);
        this.processAnnotation(PutMapping.class);
        this.processAnnotation(DeleteMapping.class);
        this.processAnnotation(PatchMapping.class);
    }

    @SuppressWarnings("unchecked")
    public void processDefinitionAnnotation() {
        List<Class<?>> routeDefinition = this.getTypesAnnotated(Route.class);
        for (Class<?> _class : routeDefinition) {
            if (RouteDefinition.class.isAssignableFrom(_class)) {
                RouteManager.routeDefinition.add(
                    (Class<? extends RouteDefinition>) _class
                );
            }
        }
    }

    public void processAnnotation(Class<? extends Annotation> annotation) {
        List<Method> methods = this.getMethodsAnnotated(annotation);
        for (Method method : methods) {
            Annotation a = AnnotationUtils.getAnnotation(method, annotation);
            if (a == null) {
                continue;
            }
            try {
                RouteManager.annotationRouteDefinitions.add(
                    new AnnotationRouteDefinition(
                        (RequestMethod[]) AnnotationUtils.getAnnotationValue(
                            a,
                            "method"
                        ),
                        (String) AnnotationUtils.getAnnotationValue(a, "value"),
                        Util.routeHandler(method)
                    )
                );
            } catch (Exception e) {
                throw new AnnotationProcessorException(
                    "Route annotation process error",
                    e
                );
            }
        }
    }
}
