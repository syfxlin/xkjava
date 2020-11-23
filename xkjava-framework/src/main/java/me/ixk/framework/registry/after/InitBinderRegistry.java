/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.ControllerAdvice;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.web.InitBinderHandlerResolver;

/**
 * InitBinderRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:54
 */
@Component(name = "initBinderRegistry")
public class InitBinderRegistry implements AfterImportBeanRegistry {
    private final List<InitBinderHandlerResolver> adviceResolvers = new ArrayList<>();
    private final Map<Class<?>, InitBinderHandlerResolver> controllerResolvers = new ConcurrentHashMap<>();

    @Override
    public void register(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    ) {
        if (annotation.hasAnnotation(ControllerAdvice.class)) {
            InitBinderHandlerResolver resolver = new InitBinderHandlerResolver(
                (Class<?>) element
            );
            if (resolver.hasInitBinderList()) {
                this.adviceResolvers.add(resolver);
            }
        }
        if (annotation.hasAnnotation(Controller.class)) {
            InitBinderHandlerResolver resolver = new InitBinderHandlerResolver(
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
