/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.AspectPointcut;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class AspectAnnotationProcessor extends AbstractAnnotationProcessor {

    public AspectAnnotationProcessor(final XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        final AspectManager aspectManager = this.app.make(AspectManager.class);
        for (final Class<?> clazz : this.getTypesAnnotated(Aspect.class)) {
            if (Advice.class.isAssignableFrom(clazz)) {
                String pointcut = AnnotationUtils.getAnnotationValue(
                    clazz,
                    Aspect.class,
                    "pointcut"
                );
                if (pointcut == null) {
                    continue;
                }
                aspectManager.addAdvice(
                    new AspectPointcut(pointcut),
                    this.app.make(clazz.getName(), Advice.class)
                );
            } else {
                throw new AnnotationProcessorException(
                    "Classes marked by the Aspect annotation should implement the Advice interface"
                );
            }
        }
    }
}
