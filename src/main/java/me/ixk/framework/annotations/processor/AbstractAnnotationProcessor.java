/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public abstract class AbstractAnnotationProcessor
    implements AnnotationProcessor {
    protected final XkJava app;

    public AbstractAnnotationProcessor(XkJava app) {
        this.app = app;
    }

    protected List<Class<?>> getTypesAnnotated(
        Class<? extends Annotation> annotation
    ) {
        return AnnotationUtils.getTypesAnnotated(annotation);
    }

    protected List<Method> getMethodsAnnotated(
        Class<? extends Annotation> annotation
    ) {
        return AnnotationUtils.getMethodsAnnotated(annotation);
    }
}
