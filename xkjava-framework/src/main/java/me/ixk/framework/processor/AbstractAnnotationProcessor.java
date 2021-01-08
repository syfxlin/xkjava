/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.ioc.BeanScanner;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 注解处理器（抽象类）
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:46
 */
public abstract class AbstractAnnotationProcessor
    implements AnnotationProcessor {

    protected final XkJava app;
    protected final BeanScanner scanner;

    public AbstractAnnotationProcessor(XkJava app) {
        this.app = app;
        this.scanner = app.beanScanner();
    }

    protected Set<Class<?>> getTypesAnnotated(
        Class<? extends Annotation> annotation
    ) {
        return this.scanner.getTypesAnnotated(annotation);
    }

    protected Set<Method> getMethodsAnnotated(
        Class<? extends Annotation> annotation
    ) {
        return this.scanner.getMethodsAnnotated(annotation);
    }

    protected String getScoopType(final MergedAnnotation annotation) {
        final String scopeType = annotation.getString(Scope.class, "type");
        return scopeType == null ? ScopeType.SINGLETON : scopeType;
    }

    @SuppressWarnings("unchecked")
    protected void processAnnotation(
        Class<? extends Annotation> annotationType,
        Consumer<Class<?>> classConsumer,
        Consumer<Method> methodConsumer
    ) {
        this.processAnnotation(
                new Class[] { annotationType },
                classConsumer,
                methodConsumer
            );
    }

    protected void processAnnotation(
        Class<? extends Annotation>[] annotationTypes,
        Consumer<Class<?>> classConsumer,
        Consumer<Method> methodConsumer
    ) {
        Set<AnnotatedElement> elements = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            elements.addAll(this.getTypesAnnotated(annotationType));
            elements.addAll(this.getMethodsAnnotated(annotationType));
        }
        this.processAnnotation(elements, classConsumer, methodConsumer);
    }

    @SuppressWarnings("unchecked")
    protected void processAnnotation(
        Set<AnnotatedElement> elements,
        Consumer<Class<?>> classConsumer,
        Consumer<Method> methodConsumer
    ) {
        elements = AnnotationUtils.sortByOrderAnnotation(elements);
        Set<AnnotatedElement> ineligible = new LinkedHashSet<>();
        while (true) {
            boolean hasAccept = false;
            for (AnnotatedElement element : elements) {
                if (this.scanner.isCondition(element)) {
                    hasAccept = true;
                    if (element instanceof Class) {
                        classConsumer.accept((Class<?>) element);
                    } else {
                        methodConsumer.accept((Method) element);
                    }
                } else {
                    ineligible.add(element);
                }
            }
            elements = ineligible;
            if (!hasAccept) {
                break;
            }
        }
    }
}
