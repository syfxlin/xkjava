/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.BeanScanner;
import me.ixk.framework.ioc.XkJava;
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

    protected ScopeType getScoopType(final MergedAnnotation annotation) {
        final ScopeType scopeType = annotation.get(Scope.class, "type");
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
                classes = this.scanner.filterConditionAnnotation(classes);
            }
        }
        Set<Method> methods = this.getMethodsAnnotated(annotationType);
        final Iterator<Method> methodIterator = methods.iterator();
        while (methodIterator.hasNext()) {
            final Method next = methodIterator.next();
            if (methods.contains(next)) {
                methodConsumer.accept(next);
                methods = this.scanner.filterConditionAnnotation(methods);
            }
        }
    }
}
