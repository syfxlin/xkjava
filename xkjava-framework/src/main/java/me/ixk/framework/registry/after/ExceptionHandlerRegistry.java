/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import java.util.Map;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.ControllerAdvice;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.ExceptionHandlerResolver;
import me.ixk.framework.utils.MergedAnnotation;

@Component(name = "exceptionHandlerRegistry")
public class ExceptionHandlerRegistry implements AfterImportBeanRegistry {
    private final Map<Class<?>, ExceptionHandlerResolver> adviceResolvers = new LinkedHashMap<>();
    private final Map<Class<?>, ExceptionHandlerResolver> controllerResolvers = new LinkedHashMap<>();

    @Override
    public void after(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    ) {
        Map<Class<?>, ExceptionHandlerResolver> resolvers = null;
        if (annotation.hasAnnotation(ControllerAdvice.class)) {
            resolvers = this.adviceResolvers;
        }
        if (annotation.hasAnnotation(Controller.class)) {
            resolvers = this.controllerResolvers;
        }
        if (resolvers == null) {
            return;
        }
        ExceptionHandlerResolver resolver = new ExceptionHandlerResolver(
            (Class<?>) element
        );
        if (resolver.hasExceptionMappings()) {
            resolvers.put((Class<?>) element, resolver);
        }
    }

    public Map<Class<?>, ExceptionHandlerResolver> getAdviceResolvers() {
        return adviceResolvers;
    }

    public Map<Class<?>, ExceptionHandlerResolver> getControllerResolvers() {
        return controllerResolvers;
    }
}
