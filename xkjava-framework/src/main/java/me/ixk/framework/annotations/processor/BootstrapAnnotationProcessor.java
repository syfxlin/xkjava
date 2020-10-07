/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import me.ixk.framework.bootstrap.Bootstrap;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;

public class BootstrapAnnotationProcessor extends AbstractAnnotationProcessor {

    public BootstrapAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        for (Class<?> bootstrapType : this.getTypesAnnotated(
                me.ixk.framework.annotations.Bootstrap.class
            )) {
            if (Bootstrap.class.isAssignableFrom(bootstrapType)) {
                this.app.call(bootstrapType, "boot", Object.class);
            } else {
                throw new AnnotationProcessorException(
                    "Classes marked by the Bootstrap annotation should implement the Bootstrap interface"
                );
            }
        }
    }
}
