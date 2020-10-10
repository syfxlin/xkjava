/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.ControllerAdvice;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.ExceptionHandlerResolver;
import me.ixk.framework.utils.MergedAnnotation;

public class ExceptionHandlerRegistrar implements AttributeRegistrar {

    @Override
    public Object register(
        XkJava app,
        String attributeName,
        AnnotatedElement element,
        ScopeType scopeType,
        MergedAnnotation annotation
    ) {
        LinkedHashMap<Object, Object> resolvers = null;
        if (annotation.hasAnnotation(ControllerAdvice.class)) {
            resolvers =
                app.getOrDefaultAttribute(attributeName, new LinkedHashMap<>());
        }
        if (annotation.hasAnnotation(Controller.class)) {
            resolvers =
                app.getOrDefaultAttribute(attributeName, new LinkedHashMap<>());
        }
        if (resolvers == null) {
            return null;
        }
        ExceptionHandlerResolver resolver = new ExceptionHandlerResolver(
            (Class<?>) element
        );
        if (resolver.hasExceptionMappings()) {
            resolvers.put(element, resolver);
        }
        return resolvers;
    }
}
