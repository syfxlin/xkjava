/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;

public abstract class AbstractAnnotationProcessor
    implements AnnotationProcessor {
    protected final XkJava app;

    public AbstractAnnotationProcessor(XkJava app) {
        this.app = app;
    }

    protected Set<Class<?>> getTypesAnnotated(
        Class<? extends Annotation> annotation
    ) {
        return AnnotationUtils.getTypesAnnotated(annotation);
    }

    protected Set<Method> getMethodsAnnotated(
        Class<? extends Annotation> annotation
    ) {
        return AnnotationUtils.getMethodsAnnotated(annotation);
    }

    protected ScopeType getScoopType(final AnnotatedElement element) {
        final ScopeType scopeType = AnnotationUtils.getAnnotationValue(
            element,
            Scope.class,
            "type"
        );
        return scopeType == null ? ScopeType.SINGLETON : scopeType;
    }

    @SuppressWarnings("unchecked")
    protected void processAnnotation(
        Class<? extends Annotation> annotationType,
        Consumer<Class<?>> classConsumer,
        Consumer<Method> methodConsumer
    ) {
        Set<Class<?>> classes = this.getTypesAnnotated(annotationType);
        final Iterator<Class<?>> classIterator = classes.iterator();
        while (classIterator.hasNext()) {
            final Class<?> next = classIterator.next();
            if (classes.contains(next)) {
                classConsumer.accept(next);
                classes = AnnotationUtils.filterConditionAnnotation(classes);
            }
        }
        Set<Method> methods = this.getMethodsAnnotated(annotationType);
        final Iterator<Method> methodIterator = methods.iterator();
        while (methodIterator.hasNext()) {
            final Method next = methodIterator.next();
            if (methods.contains(next)) {
                methodConsumer.accept(next);
                methods = AnnotationUtils.filterConditionAnnotation(methods);
            }
        }
    }
}
