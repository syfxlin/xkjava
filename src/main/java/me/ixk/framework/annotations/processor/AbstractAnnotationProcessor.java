package me.ixk.framework.annotations.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ReflectionsUtils;
import org.reflections.Reflections;

public abstract class AbstractAnnotationProcessor
    implements AnnotationProcessor {
    protected final Application app;

    protected final Reflections reflections;

    public AbstractAnnotationProcessor(Application app) {
        this.app = app;
        this.reflections = ReflectionsUtils.make();
    }

    @SuppressWarnings("unchecked")
    protected List<Class<?>> getTypesAnnotatedWith(
        Class<? extends Annotation> annotation
    ) {
        return (List<Class<?>>) AnnotationUtils.sortByOrderAnnotation(
            this.reflections.getTypesAnnotatedWith(annotation)
        );
    }

    @SuppressWarnings("unchecked")
    protected List<Method> getMethodsAnnotatedWith(
        Class<? extends Annotation> annotation
    ) {
        return (List<Method>) AnnotationUtils.sortByOrderAnnotation(
            this.reflections.getMethodsAnnotatedWith(annotation)
        );
    }
}
