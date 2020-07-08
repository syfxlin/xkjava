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

import java.util.List;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class AspectAnnotationProcessor extends AbstractAnnotationProcessor {

    public AspectAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        List<Class<?>> classes = this.getTypesAnnotated(Aspect.class);
        for (Class<?> _class : classes) {
            if (Advice.class.isAssignableFrom(_class)) {
                Aspect aspect = AnnotationUtils.getAnnotation(
                    _class,
                    Aspect.class
                );
                AspectManager.addAdvice(
                    new AspectPointcut(aspect.value()),
                    this.app.make(_class.getName(), Advice.class)
                );
            } else {
                throw new AnnotationProcessorException(
                    "Classes marked by the Aspect annotation should implement the Advice interface"
                );
            }
        }
    }
}
