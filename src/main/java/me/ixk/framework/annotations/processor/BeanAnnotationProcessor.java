package me.ixk.framework.annotations.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.ixk.framework.annotations.*;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.AnnotationUtils;

public class BeanAnnotationProcessor extends AbstractAnnotationProcessor {
    protected List<Class<? extends Annotation>> notSharedAnnotations = Arrays.asList(
        Repository.class,
        Service.class,
        Mapper.class
    );

    protected List<Class<? extends Annotation>> aliasAnnotations = Arrays.asList(
        Bean.class,
        Component.class,
        Controller.class,
        Repository.class,
        Service.class,
        Mapper.class
    );

    public BeanAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process() {
        List<Class<? extends Annotation>> beanAnnotations = Config.get(
            "app.bean_annotations",
            List.class
        );
        for (Class<? extends Annotation> annotation : beanAnnotations) {
            for (Class<?> _class : this.reflections.getTypesAnnotatedWith(
                    annotation
                )) {
                this.processAnnotation(annotation, _class);
            }
            for (Method method : this.reflections.getMethodsAnnotatedWith(
                    annotation
                )) {
                this.processAnnotation(annotation, method);
            }
        }
    }

    private void processAnnotation(
        Class<? extends Annotation> annotation,
        Method method
    ) {
        boolean isShared = !this.notSharedAnnotations.contains(annotation);
        if (method.isAnnotationPresent(Scope.class)) {
            isShared =
                AnnotationUtils
                    .getAnnotation(method, Scope.class)
                    .value()
                    .equals("singleton");
        }
        String name = method.getName();
        Class<?> _class = method.getReturnType();
        if (this.aliasAnnotations.contains(annotation)) {
            Annotation anno = AnnotationUtils.getAnnotation(method, annotation);
            this.app.bind(
                    _class,
                    (container, args) ->
                        method.invoke(
                            container.make(method.getDeclaringClass())
                        ),
                    isShared
                );
            for (String n : (String[]) Objects.requireNonNull(
                AnnotationUtils.getAnnotationValue(anno, "value")
            )) {
                this.app.bind(
                        _class,
                        (container, args) ->
                            method.invoke(
                                container.make(method.getDeclaringClass())
                            ),
                        isShared,
                        n
                    );
            }
        } else {
            this.app.bind(
                    name,
                    (container, args) ->
                        method.invoke(
                            container.make(method.getDeclaringClass())
                        ),
                    isShared
                );
        }
    }

    private void processAnnotation(
        Class<? extends Annotation> annotation,
        Class<?> _class
    ) {
        boolean isShared = !this.notSharedAnnotations.contains(annotation);
        if (_class.isAnnotationPresent(Scope.class)) {
            isShared =
                AnnotationUtils
                    .getAnnotation(_class, Scope.class)
                    .value()
                    .equals("singleton");
        }
        if (this.aliasAnnotations.contains(annotation)) {
            Annotation anno = AnnotationUtils.getAnnotation(_class, annotation);
            this.app.bind(_class, _class, isShared);
            for (String name : (String[]) Objects.requireNonNull(
                AnnotationUtils.getAnnotationValue(anno, "value")
            )) {
                this.app.bind(_class, _class, isShared, name);
            }
        } else {
            this.app.bind(_class, _class, isShared);
        }
    }
}
