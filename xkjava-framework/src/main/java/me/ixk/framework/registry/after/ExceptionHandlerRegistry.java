/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.annotation.web.Controller;
import me.ixk.framework.annotation.web.ControllerAdvice;
import me.ixk.framework.annotation.web.ExceptionHandler;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExceptionHandlerRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:54
 */
@Component(name = "exceptionHandlerRegistry")
public class ExceptionHandlerRegistry implements AfterBeanRegistry {

    private static final Logger log = LoggerFactory.getLogger(
        ExceptionHandlerRegistry.class
    );
    /**
     * Controller, [Exception, Resolver Method]
     */
    private final Map<Class<?>, Map<Class<? extends Throwable>, Method>> adviceResolvers = new LinkedHashMap<>();
    private final Map<Class<?>, Map<Class<? extends Throwable>, Method>> controllerResolvers = new LinkedHashMap<>();

    @Override
    public void register(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    ) {
        Map<Class<?>, Map<Class<? extends Throwable>, Method>> resolvers = null;
        if (annotation.hasAnnotation(ControllerAdvice.class)) {
            resolvers = this.adviceResolvers;
        }
        if (annotation.hasAnnotation(Controller.class)) {
            resolvers = this.controllerResolvers;
        }
        if (resolvers == null) {
            return;
        }
        Map<Class<? extends Throwable>, Method> resolverMap =
            this.resolveResolver((Class<?>) element);
        if (!resolverMap.isEmpty()) {
            resolvers.put((Class<?>) element, resolverMap);
        }
    }

    private Map<Class<? extends Throwable>, Method> resolveResolver(
        Class<?> clazz
    ) {
        Map<Class<? extends Throwable>, Method> map = new ConcurrentHashMap<>(
            16
        );
        for (Method method : clazz.getDeclaredMethods()) {
            ExceptionHandler exceptionHandler = MergedAnnotation
                .from(method)
                .getAnnotation(ExceptionHandler.class);
            if (exceptionHandler != null) {
                for (Class<? extends Throwable> exceptionType : exceptionHandler.exception()) {
                    Method oldMethod = map.put(exceptionType, method);
                    if (oldMethod != null && !oldMethod.equals(method)) {
                        log.error(
                            "Ambiguous @ExceptionHandler method mapped for [{}]: ({},{})",
                            exceptionType,
                            oldMethod,
                            method
                        );
                        throw new IllegalStateException(
                            "Ambiguous @ExceptionHandler method mapped for [" +
                            exceptionType +
                            "]: (" +
                            oldMethod +
                            ", " +
                            method +
                            ")"
                        );
                    }
                }
            }
        }
        return map;
    }

    public Map<Class<?>, Map<Class<? extends Throwable>, Method>> getAdviceResolvers() {
        return adviceResolvers;
    }

    public Map<Class<? extends Throwable>, Method> getControllerResolver(
        Class<?> clazz
    ) {
        return controllerResolvers.get(clazz);
    }
}
