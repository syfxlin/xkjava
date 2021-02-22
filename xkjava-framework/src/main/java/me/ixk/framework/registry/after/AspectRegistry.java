/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotation.core.Aspect;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.AspectPointcut;
import me.ixk.framework.exception.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AspectRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:59
 */
public class AspectRegistry implements AfterBeanRegistry {

    private static final Logger log = LoggerFactory.getLogger(
        AspectRegistry.class
    );

    @Override
    @SuppressWarnings("unchecked")
    public void register(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        final AspectManager aspectManager = app.make(AspectManager.class);
        if (Advice.class.isAssignableFrom((Class<?>) element)) {
            final String pointcut = annotation.getString(
                Aspect.class,
                "pointcut"
            );
            if (pointcut == null) {
                return;
            }
            aspectManager.addAdvice(
                new AspectPointcut(pointcut),
                (Class<? extends Advice>) element
            );
        } else {
            log.error(
                "Classes marked by the Aspect annotation should implement the Advice interface"
            );
            throw new AnnotationProcessorException(
                "Classes marked by the Aspect annotation should implement the Advice interface"
            );
        }
    }
}
