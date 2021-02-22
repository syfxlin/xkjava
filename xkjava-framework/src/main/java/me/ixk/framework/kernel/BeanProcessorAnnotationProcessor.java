/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import cn.hutool.core.util.ReflectUtil;
import me.ixk.framework.annotation.core.BeanProcessor;
import me.ixk.framework.exception.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.processor.BeanAfterCreateProcessor;
import me.ixk.framework.ioc.processor.BeanDestroyProcessor;
import me.ixk.framework.ioc.processor.BeforeInjectProcessor;
import me.ixk.framework.processor.AbstractAnnotationProcessor;
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
            if (BeforeInjectProcessor.class.isAssignableFrom(processorType)) {
                this.app.addBeforeInjectProcessor(
                        (BeforeInjectProcessor) ReflectUtil.newInstance(
                            processorType
                        )
                    );
                continue;
            }
            if (
                BeanAfterCreateProcessor.class.isAssignableFrom(processorType)
            ) {
                this.app.addBeanAfterCreateProcessor(
                        (BeanAfterCreateProcessor) ReflectUtil.newInstance(
                            processorType
                        )
                    );
                continue;
            }
            if (BeanDestroyProcessor.class.isAssignableFrom(processorType)) {
                this.app.addBeanDestroyProcessor(
                        (BeanDestroyProcessor) ReflectUtil.newInstance(
                            processorType
                        )
                    );
                continue;
            }
            log.error(
                "Classes marked by the BeanProcessor annotation should implement the BeanBeforeProcessor or BeanAfterProcessor interface"
            );
            throw new AnnotationProcessorException(
                "Classes marked by the BeanProcessor annotation should implement the BeanBeforeProcessor or BeanAfterProcessor interface"
            );
        }
    }
}
