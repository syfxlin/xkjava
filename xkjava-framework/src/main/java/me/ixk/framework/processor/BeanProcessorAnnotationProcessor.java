/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import cn.hutool.core.util.ReflectUtil;
import me.ixk.framework.annotations.BeanProcessor;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.BeanAfterProcessor;
import me.ixk.framework.ioc.BeanBeforeProcessor;
import me.ixk.framework.ioc.XkJava;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BeanProcessor 注解处理器
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 8:56
 */
public class BeanProcessorAnnotationProcessor
    extends AbstractAnnotationProcessor {
    private static final Logger log = LoggerFactory.getLogger(
        BeanProcessorAnnotationProcessor.class
    );

    public BeanProcessorAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        for (Class<?> processorType : this.getTypesAnnotated(
                BeanProcessor.class
            )) {
            if (BeanBeforeProcessor.class.isAssignableFrom(processorType)) {
                this.app.addBeanBeforeProcessor(
                        (BeanBeforeProcessor) ReflectUtil.newInstance(
                            processorType
                        )
                    );
            } else if (
                BeanAfterProcessor.class.isAssignableFrom(processorType)
            ) {
                this.app.addBeanAfterProcessor(
                        (BeanAfterProcessor) ReflectUtil.newInstance(
                            processorType
                        )
                    );
            } else {
                log.error(
                    "Classes marked by the BeanProcessor annotation should implement the BeanBeforeProcessor or BeanAfterProcessor interface"
                );
                throw new AnnotationProcessorException(
                    "Classes marked by the BeanProcessor annotation should implement the BeanBeforeProcessor or BeanAfterProcessor interface"
                );
            }
        }
    }
}
