/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.annotation.web.Controller;
import me.ixk.framework.annotation.web.ControllerAdvice;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;
import me.ixk.framework.web.resolver.InitBinderHandlerResolver;

/**
 * InitBinderRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:54
 */
@Component(name = "initBinderRegistry")
public class InitBinderRegistry implements AfterBeanRegistry {

    private final List<InitBinderHandlerResolver> adviceResolvers = new ArrayList<>();
    private final Map<Class<?>, InitBinderHandlerResolver> controllerResolvers = new ConcurrentHashMap<>();

    @Override
    public void register(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        if (annotation.hasAnnotation(ControllerAdvice.class)) {
            final InitBinderHandlerResolver resolver = new InitBinderHandlerResolver(
                (Class<?>) element
            );
            if (resolver.hasInitBinderList()) {
                this.adviceResolvers.add(resolver);
            }
        }
        if (annotation.hasAnnotation(Controller.class)) {
            final InitBinderHandlerResolver resolver = new InitBinderHandlerResolver(
                (Class<?>) element
            );
            if (resolver.hasInitBinderList()) {
                this.controllerResolvers.put((Class<?>) element, resolver);
            }
        }
    }

    public List<InitBinderHandlerResolver> getAdviceResolvers() {
        return adviceResolvers;
    }

    public Map<Class<?>, InitBinderHandlerResolver> getControllerResolvers() {
        return controllerResolvers;
    }
}
