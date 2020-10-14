/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import me.ixk.framework.bootstrap.Bootstrap;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 启动注解处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:50
 */
public class BootstrapAnnotationProcessor extends AbstractAnnotationProcessor {
    private static final Logger log = LoggerFactory.getLogger(
        BootstrapAnnotationProcessor.class
    );

    public BootstrapAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        for (Class<?> bootstrapType : this.getTypesAnnotated(
                me.ixk.framework.annotations.Bootstrap.class
            )) {
            if (Bootstrap.class.isAssignableFrom(bootstrapType)) {
                this.app.call(bootstrapType, "boot", Void.class);
            } else {
                log.error(
                    "Classes marked by the Bootstrap annotation should implement the Bootstrap interface"
                );
                throw new AnnotationProcessorException(
                    "Classes marked by the Bootstrap annotation should implement the Bootstrap interface"
                );
            }
        }
    }
}
