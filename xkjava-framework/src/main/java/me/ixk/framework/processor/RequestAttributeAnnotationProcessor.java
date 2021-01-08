/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import java.lang.reflect.Method;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RequestAttribute;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.registry.request.RequestAttributeRegistry;
import me.ixk.framework.utils.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request 属性注解处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:50
 */
@AnnotationProcessor
@Order(Order.MEDIUM_PRECEDENCE + 2)
public class RequestAttributeAnnotationProcessor
    extends AbstractAnnotationProcessor {

    private static final Logger log = LoggerFactory.getLogger(
        RequestAttributeAnnotationProcessor.class
    );

    public RequestAttributeAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        this.processAnnotation(
                RequestAttribute.class,
                clazz -> {
                    log.error(
                        "RequestAttribute unsupported annotation in class: {}",
                        clazz
                    );
                    throw new UnsupportedOperationException(
                        "RequestAttribute unsupported annotation in class"
                    );
                },
                this::processAnnotation
            );
    }

    @SuppressWarnings("unchecked")
    public void processAnnotation(Method method) {
        MergedAnnotation annotation = MergedAnnotation.from(method);
        this.app.make(me.ixk.framework.web.RequestAttributeRegistry.class)
            .addRegistry(
                method,
                annotation,
                this.app.make(
                        (Class<RequestAttributeRegistry>) annotation.get(
                            RequestAttribute.class,
                            "registry"
                        )
                    )
            );
    }
}
