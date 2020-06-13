package me.ixk.framework.annotations.processor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.ControllerAdvice;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.ExceptionHandlerResolver;
import me.ixk.framework.utils.AnnotationUtils;

public class ExceptionHandlerAnnotationProcessor
    extends AbstractAnnotationProcessor {

    public ExceptionHandlerAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        Map<Class<?>, ExceptionHandlerResolver> handlerResolvers = new LinkedHashMap<>();
        Set<Class<?>> controllerAdvices =
            this.reflections.getTypesAnnotatedWith(ControllerAdvice.class);
        for (Class<?> adviceType : AnnotationUtils.sortByOrderAnnotation(
            controllerAdvices
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
        Set<Class<?>> controllers =
            this.reflections.getTypesAnnotatedWith(Controller.class);
        for (Class<?> controller : controllers) {
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
