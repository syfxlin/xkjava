package me.ixk.framework.annotations.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.AnnotationUtils;

public class BeanAnnotationProcessor extends AbstractAnnotationProcessor {

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
        Scope scope = annotation.getAnnotation(Scope.class);
        boolean isShared =
            scope == null || scope.value() == ScopeType.SINGLETON;
        if (method.isAnnotationPresent(Scope.class)) {
            isShared =
                AnnotationUtils.getAnnotation(method, Scope.class).value() ==
                ScopeType.SINGLETON;
        }
        String name = method.getName();
        Class<?> _class = method.getReturnType();
        Annotation anno = AnnotationUtils.getAnnotation(method, annotation);
        Boolean bindType = (Boolean) AnnotationUtils.getAnnotationValue(
            anno,
            "bindType"
        );
        this.app.bind(
                name,
                (container, args) ->
                    method.invoke(container.make(method.getDeclaringClass())),
                isShared,
                bindType == null || !bindType ? null : _class.getName()
            );
        Object names = AnnotationUtils.getAnnotationValue(anno, "name");
        if (names != null) {
            for (String n : (String[]) names) {
                this.app.bind(
                        n,
                        (container, args) ->
                            method.invoke(
                                container.make(method.getDeclaringClass())
                            ),
                        isShared
                    );
            }
        }
    }

    private void processAnnotation(
        Class<? extends Annotation> annotation,
        Class<?> _class
    ) {
        Scope scope = annotation.getAnnotation(Scope.class);
        boolean isShared =
            scope == null || scope.value() == ScopeType.SINGLETON;
        if (_class.isAnnotationPresent(Scope.class)) {
            isShared =
                AnnotationUtils.getAnnotation(_class, Scope.class).value() ==
                ScopeType.SINGLETON;
        }
        Annotation anno = AnnotationUtils.getAnnotation(_class, annotation);
        this.app.bind(_class, _class, isShared);
        Object names = AnnotationUtils.getAnnotationValue(anno, "name");
        if (names != null) {
            for (String name : (String[]) names) {
                this.app.bind(_class, _class, isShared, name);
            }
        }
    }
}
