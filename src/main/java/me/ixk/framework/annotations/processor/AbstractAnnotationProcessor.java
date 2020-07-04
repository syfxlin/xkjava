/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.AnnotationUtils;

public abstract class AbstractAnnotationProcessor
    implements AnnotationProcessor {
    protected final Application app;

    public AbstractAnnotationProcessor(Application app) {
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
