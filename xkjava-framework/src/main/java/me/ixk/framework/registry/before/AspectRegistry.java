/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.before;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.AspectPointcut;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AspectRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:59
 */
public class AspectRegistry implements BeforeImportBeanRegistry {
    private static final Logger log = LoggerFactory.getLogger(
        AspectRegistry.class
    );

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
            log.error(
                "Classes marked by the Aspect annotation should implement the Advice interface"
            );
            throw new AnnotationProcessorException(
                "Classes marked by the Aspect annotation should implement the Advice interface"
            );
        }
    }
}
