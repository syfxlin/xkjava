/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.AspectPointcut;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

public class AspectRegistrar implements BeforeImportBeanRegistrar {

    @Override
    public void before(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    ) {
        final AspectManager aspectManager = app.make(AspectManager.class);
        if (Advice.class.isAssignableFrom((Class<?>) element)) {
            String pointcut = annotation.get(Aspect.class, "pointcut");
            if (pointcut == null) {
                return;
            }
            aspectManager.addAdvice(
                new AspectPointcut(pointcut),
                app.make(((Class<?>) element).getName(), Advice.class)
            );
        } else {
            throw new AnnotationProcessorException(
                "Classes marked by the Aspect annotation should implement the Advice interface"
            );
        }
    }
}
