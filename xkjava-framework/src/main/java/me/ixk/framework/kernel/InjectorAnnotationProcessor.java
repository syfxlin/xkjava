/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import cn.hutool.core.util.ReflectUtil;
import me.ixk.framework.annotation.Injector;
import me.ixk.framework.exception.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.injector.InstanceInjector;
import me.ixk.framework.ioc.injector.ParameterInjector;
import me.ixk.framework.processor.AbstractAnnotationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注入器注解处理器
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 8:55
 */
public class InjectorAnnotationProcessor extends AbstractAnnotationProcessor {

    private static final Logger log = LoggerFactory.getLogger(
        InjectorAnnotationProcessor.class
    );

    public InjectorAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        for (Class<?> processorType : this.getTypesAnnotated(Injector.class)) {
            if (ParameterInjector.class.isAssignableFrom(processorType)) {
                this.app.addParameterInjector(
                        (ParameterInjector) ReflectUtil.newInstance(
                            processorType
                        )
                    );
                continue;
            }
            if (InstanceInjector.class.isAssignableFrom(processorType)) {
                this.app.addInstanceInjector(
                        (InstanceInjector) ReflectUtil.newInstance(
                            processorType
                        )
                    );
                continue;
            }
            log.error(
                "Classes marked by the Injector annotation should implement the ParameterInjector or InstanceInjector interface"
            );
            throw new AnnotationProcessorException(
                "Classes marked by the Injector annotation should implement the ParameterInjector or InstanceInjector interface"
            );
        }
    }
}
