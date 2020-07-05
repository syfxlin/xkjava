/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import static me.ixk.framework.helpers.FacadeHelper.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.ixk.framework.annotations.*;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.ioc.Wrapper;
import me.ixk.framework.utils.AnnotationUtils;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class BeanAnnotationProcessor extends AbstractAnnotationProcessor {
    private final List<String> makeList = new ArrayList<>();

    public BeanAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process() {
        List<Class<? extends Annotation>> beanAnnotations = config()
            .get("app.bean_annotations", List.class);
        // bind
        for (Class<? extends Annotation> annotation : beanAnnotations) {
            for (Class<?> _class : this.getTypesAnnotated(annotation)) {
                this.processAnnotation(annotation, _class);
            }
            for (Method method : this.getMethodsAnnotated(annotation)) {
                this.processAnnotation(annotation, method);
            }
        }
        // make
        for (String name : this.makeList) {
            this.app.make(name);
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
        Bean.BindType bindType = (Bean.BindType) AnnotationUtils.getAnnotationValue(
            anno,
            "bindType"
        );
        Wrapper wrapper = (container, with) ->
            method.invoke(container.make(method.getDeclaringClass()));
        this.app.bind(
                name,
                wrapper,
                // Method 的 Bean 默认是不绑定 Type 的
                (bindType == Bean.BindType.BIND) ? _class.getName() : null,
                scopeType
            );
        Object names = AnnotationUtils.getAnnotationValue(anno, "name");
        if (names != null) {
            for (String n : (String[]) names) {
                this.app.bind(n, wrapper, null, scopeType);
            }
        }
        if (
            scopeType.isSingleton() &&
            method.getAnnotation(Lazy.class) == null &&
            anno.annotationType().getAnnotation(Lazy.class) == null
        ) {
            makeList.add(name);
        }
    }

    private void processAnnotation(
        Class<? extends Annotation> annotation,
        Class<?> _class
    ) {
        ScopeType scopeType = this.getScoopType(annotation, _class);
        Annotation anno = AnnotationUtils.getAnnotation(_class, annotation);
        Bean.BindType bindType = (Bean.BindType) AnnotationUtils.getAnnotationValue(
            anno,
            "bindType"
        );
        // Class 的 Bean 默认绑定 Type
        if (
            bindType == null ||
            bindType == Bean.BindType.NO_SET ||
            bindType == Bean.BindType.BIND
        ) {
            this.app.bind(_class, _class, null, scopeType);
        }
        Object names = AnnotationUtils.getAnnotationValue(anno, "name");
        if (names != null) {
            for (String name : (String[]) names) {
                this.app.bind(name, _class.getName(), null, scopeType);
            }
        }
        if (
            scopeType.isSingleton() &&
            _class.getAnnotation(Lazy.class) == null &&
            anno.annotationType().getAnnotation(Lazy.class) == null
        ) {
            makeList.add(_class.getName());
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
