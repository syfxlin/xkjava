/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import java.util.Set;
import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.processor.AnnotationProcessor;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.utils.ReflectionsUtils;

@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 4)
public class ProcessAnnotation extends AbstractBootstrap {

    public ProcessAnnotation(XkJava app) {
        super(app);
    }

    @Override
    public void boot() {
        Set<Class<?>> processors = ReflectionsUtils.getTypesAnnotatedWith(
            me.ixk.framework.annotations.AnnotationProcessor.class
        );
        for (Class<?> processorType : processors) {
            if (!AnnotationProcessor.class.isAssignableFrom(processorType)) {
                throw new AnnotationProcessorException(
                    "Classes marked by the AnnotationProcessor annotation should implement the AnnotationProcessor interface"
                );
            }
        }
        AnnotationProcessorManager manager =
            this.app.annotationProcessorManager();
        manager.registers(processors);
        manager.process();
    }
}
