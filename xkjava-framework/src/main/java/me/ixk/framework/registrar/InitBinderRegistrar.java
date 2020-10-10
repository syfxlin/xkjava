/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.ControllerAdvice;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.InitBinderHandlerResolver;
import me.ixk.framework.utils.MergeAnnotation;

public class InitBinderRegistrar implements AttributeRegistrar {

    @Override
    public Object register(
        XkJava app,
        String attributeName,
        AnnotatedElement element,
        ScopeType scopeType,
        MergeAnnotation annotation
    ) {
        if (annotation.hasAnnotation(ControllerAdvice.class)) {
            List<InitBinderHandlerResolver> handlerResolvers = app.getOrDefaultAttribute(
                attributeName,
                new ArrayList<>()
            );
            InitBinderHandlerResolver resolver = new InitBinderHandlerResolver(
                (Class<?>) element
            );
            if (resolver.hasInitBinderList()) {
                handlerResolvers.add(resolver);
            }
            return handlerResolvers;
        }
        if (annotation.hasAnnotation(Controller.class)) {
            Map<Class<?>, InitBinderHandlerResolver> controllerResolvers = app.getOrDefaultAttribute(
                attributeName,
                new LinkedHashMap<>()
            );
            InitBinderHandlerResolver resolver = new InitBinderHandlerResolver(
                (Class<?>) element
            );
            if (resolver.hasInitBinderList()) {
                controllerResolvers.put((Class<?>) element, resolver);
            }
            return controllerResolvers;
        }
        return null;
    }
}
