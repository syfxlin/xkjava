/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import java.util.LinkedHashMap;
import java.util.Map;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.ControllerAdvice;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.ExceptionHandlerResolver;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 3)
public class ExceptionHandlerAnnotationProcessor
    extends AbstractAnnotationProcessor {

    public ExceptionHandlerAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        Map<Class<?>, ExceptionHandlerResolver> handlerResolvers = new LinkedHashMap<>();
        for (Class<?> adviceType : this.getTypesAnnotated(
                ControllerAdvice.class
            )) {
            ExceptionHandlerResolver resolver = new ExceptionHandlerResolver(
                adviceType
            );
            if (resolver.hasExceptionMappings()) {
                handlerResolvers.put(adviceType, resolver);
            }
        }
        this.app.setAttribute(
                "adviceExceptionHandlerResolvers",
                handlerResolvers
            );

        Map<Class<?>, ExceptionHandlerResolver> controllerResolvers = new LinkedHashMap<>();
        for (Class<?> controller : this.getTypesAnnotated(Controller.class)) {
            ExceptionHandlerResolver resolver = new ExceptionHandlerResolver(
                controller
            );
            if (resolver.hasExceptionMappings()) {
                controllerResolvers.put(controller, resolver);
            }
        }
        this.app.setAttribute(
                "controllerExceptionHandlerResolvers",
                controllerResolvers
            );
    }
}
