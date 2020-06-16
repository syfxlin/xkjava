package me.ixk.framework.annotations.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.ioc.Concrete;
import me.ixk.framework.utils.AnnotationUtils;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 1)
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
            for (Class<?> _class : this.getTypesAnnotatedWith(annotation)) {
                this.processAnnotation(annotation, _class);
            }
            for (Method method : this.getMethodsAnnotatedWith(annotation)) {
                this.processAnnotation(annotation, method);
            }
        }
    }

    private void processAnnotation(
        Class<? extends Annotation> annotation,
        Method method
    ) {
        ScopeType scopeType = this.getScoopType(annotation, method);
        String name = method.getName();
        Class<?> _class = method.getReturnType();
        Annotation anno = AnnotationUtils.getAnnotation(method, annotation);
        Boolean bindType = (Boolean) AnnotationUtils.getAnnotationValue(
            anno,
            "bindType"
        );
        Concrete concrete = (container, args) ->
            method.invoke(container.make(method.getDeclaringClass()));
        this.app.bind(
                name,
                concrete,
                scopeType,
                // Method 的 Bean 默认是不绑定 Type 的
                (bindType == null || !bindType) ? null : _class.getName()
            );
        Object names = AnnotationUtils.getAnnotationValue(anno, "name");
        if (names != null) {
            for (String n : (String[]) names) {
                this.app.bind(n, concrete, scopeType);
            }
        }
    }

    private void processAnnotation(
        Class<? extends Annotation> annotation,
        Class<?> _class
    ) {
        ScopeType scopeType = this.getScoopType(annotation, _class);
        Annotation anno = AnnotationUtils.getAnnotation(_class, annotation);
        Boolean bindType = (Boolean) AnnotationUtils.getAnnotationValue(
            anno,
            "bindType"
        );
        // Class 的 Bean 默认绑定 Type
        if (bindType == null || bindType) {
            this.app.bind(_class, _class, scopeType);
        }
        Object names = AnnotationUtils.getAnnotationValue(anno, "name");
        if (names != null) {
            for (String name : (String[]) names) {
                this.app.bind(name, _class.getName(), scopeType);
            }
        }
    }

    private ScopeType getScoopType(
        Class<? extends Annotation> annotation,
        AnnotatedElement element
    ) {
        Scope scope = annotation.getAnnotation(Scope.class);
        ScopeType scopeType = scope == null
            ? ScopeType.SINGLETON
            : scope.value();
        if (element.isAnnotationPresent(Scope.class)) {
            scopeType =
                AnnotationUtils.getAnnotation(element, Scope.class).value();
        }
        return scopeType;
    }
}
