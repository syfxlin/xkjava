package me.ixk.framework.annotations.processor;

import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.annotations.*;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.route.AnnotationMiddlewareDefinition;
import me.ixk.framework.route.RouteManager;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.Helper;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 6)
public class MiddlewareAnnotationProcessor extends AbstractAnnotationProcessor {

    public MiddlewareAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process() {
        // define
        List<Class<?>> globalMiddleware =
            this.getTypesAnnotated(GlobalMiddleware.class);
        for (Class<?> _class : globalMiddleware) {
            if (
                me.ixk.framework.middleware.Middleware.class.isAssignableFrom(
                        _class
                    )
            ) {
                RouteManager.globalMiddleware.add(
                    (Class<? extends me.ixk.framework.middleware.Middleware>) _class
                );
            }
        }
        List<Class<?>> routeMiddleware =
            this.getTypesAnnotated(RouteMiddleware.class);
        for (Class<?> _class : routeMiddleware) {
            if (
                me.ixk.framework.middleware.Middleware.class.isAssignableFrom(
                        _class
                    )
            ) {
                String name = AnnotationUtils
                    .getAnnotation(_class, RouteMiddleware.class)
                    .name();
                RouteManager.routeMiddleware.put(
                    name,
                    (Class<? extends me.ixk.framework.middleware.Middleware>) _class
                );
            }
        }
        // use
        List<Method> methods = this.getMethodsAnnotated(Middleware.class);
        for (Method method : methods) {
            Middleware annotation = AnnotationUtils.getAnnotation(
                method,
                Middleware.class
            );
            if (annotation == null) {
                continue;
            }
            try {
                String handler = Helper.routeHandler(method);
                RouteManager.annotationMiddlewareDefinitions.put(
                    handler,
                    new AnnotationMiddlewareDefinition(
                        annotation.value(),
                        annotation.middleware(),
                        handler
                    )
                );
            } catch (Exception e) {
                throw new AnnotationProcessorException(
                    "Middleware annotation process error",
                    e
                );
            }
        }
    }
}
