package me.ixk.framework.annotations.processor;

import me.ixk.framework.annotations.Middleware;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.route.AnnotationMiddlewareDefinition;
import me.ixk.framework.route.RouteManager;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.Helper;

import java.lang.reflect.Method;
import java.util.Set;

public class MiddlewareAnnotationProcessor extends AbstractAnnotationProcessor {

    public MiddlewareAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        Set<Method> methods =
            this.reflections.getMethodsAnnotatedWith(Middleware.class);
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
