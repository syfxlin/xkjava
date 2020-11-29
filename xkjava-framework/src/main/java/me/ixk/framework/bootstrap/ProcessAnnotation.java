/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import java.util.Set;
import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.processor.AnnotationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理注解
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:54
 */
@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 3)
public class ProcessAnnotation extends AbstractBootstrap {

    private static final Logger log = LoggerFactory.getLogger(
        ProcessAnnotation.class
    );

    public ProcessAnnotation(XkJava app) {
        super(app);
    }

    @Override
    public void boot() {
        // 扫描注解处理器
        Set<Class<?>> processors =
            this.scanner.getTypesAnnotated(
                    me.ixk.framework.annotations.AnnotationProcessor.class
                );
        for (Class<?> processorType : processors) {
            if (!AnnotationProcessor.class.isAssignableFrom(processorType)) {
                log.error(
                    "Classes marked by the AnnotationProcessor annotation should implement the AnnotationProcessor interface"
                );
                throw new AnnotationProcessorException(
                    "Classes marked by the AnnotationProcessor annotation should implement the AnnotationProcessor interface"
                );
            }
        }
        AnnotationProcessorManager manager =
            this.app.annotationProcessorManager();
        // 注册
        manager.registers(processors);
        // 处理
        manager.process();
    }
}
