package me.ixk.framework.annotations.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import me.ixk.framework.annotations.RequestMapping;
import me.ixk.framework.annotations.RequestMethod;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.route.AnnotationRouteDefinition;
import me.ixk.framework.route.RouteManager;

public class RouteAnnotationProcessor extends AbstractAnnotationProcessor {

    public RouteAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        this.processAnnotation(RequestMapping.class);
    }

    public void processAnnotation(Class<? extends Annotation> annotation) {
        Set<Method> methods =
            this.reflections.getMethodsAnnotatedWith(annotation);
        for (Method method : methods) {
            Annotation a = method.getAnnotation(annotation);
            Class<? extends Annotation> aClass = a.getClass();
            try {
                RouteManager.annotationRouteDefinitions.add(
                    new AnnotationRouteDefinition(
                        (RequestMethod[]) aClass.getMethod("method").invoke(a),
                        (String) aClass.getMethod("value").invoke(a),
                        (request, response) ->
                            this.app.call(method, Object.class)
                    )
                );
            } catch (Exception e) {}
        }
    }
}
