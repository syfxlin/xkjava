package me.ixk.framework.annotations.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.annotations.*;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.route.AnnotationRouteDefinition;
import me.ixk.framework.route.RouteManager;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.Helper;

public class RouteAnnotationProcessor extends AbstractAnnotationProcessor {

    public RouteAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        this.processAnnotation(RequestMapping.class);
        this.processAnnotation(GetMapping.class, RequestMethod.GET);
        this.processAnnotation(PostMapping.class, RequestMethod.POST);
        this.processAnnotation(PutMapping.class, RequestMethod.PUT);
        this.processAnnotation(DeleteMapping.class, RequestMethod.DELETE);
        this.processAnnotation(PatchMapping.class, RequestMethod.PATCH);
    }

    public void processAnnotation(
        Class<? extends Annotation> annotation,
        RequestMethod... requestMethod
    ) {
        List<Method> methods = this.getMethodsAnnotatedWith(annotation);
        for (Method method : methods) {
            Annotation a = AnnotationUtils.getAnnotation(method, annotation);
            if (a == null) {
                continue;
            }
            Class<? extends Annotation> aClass = a.getClass();
            try {
                RouteManager.annotationRouteDefinitions.add(
                    new AnnotationRouteDefinition(
                        requestMethod.length > 0
                            ? requestMethod
                            : (RequestMethod[]) aClass
                                .getMethod("method")
                                .invoke(a),
                        (String) aClass.getMethod("value").invoke(a),
                        Helper.routeHandler(method)
                    )
                );
            } catch (Exception e) {
                throw new AnnotationProcessorException(
                    "Route annotation process error",
                    e
                );
            }
        }
    }
}
