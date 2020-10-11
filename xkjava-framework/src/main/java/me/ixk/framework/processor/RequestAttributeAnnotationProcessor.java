/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import java.lang.reflect.Method;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RequestAttribute;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.registrar.RequestAttributeRegistrar;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.web.RequestAttributeRegistry;

@AnnotationProcessor
@Order(Order.MEDIUM_PRECEDENCE + 2)
public class RequestAttributeAnnotationProcessor
    extends AbstractAnnotationProcessor {

    public RequestAttributeAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        this.processAnnotation(
                RequestAttribute.class,
                clazz -> {
                    throw new UnsupportedOperationException(
                        "RequestAttribute unsupported annotation in class"
                    );
                },
                this::processAnnotation
            );
    }

    @SuppressWarnings("unchecked")
    public void processAnnotation(Method method) {
        MergedAnnotation annotation = AnnotationUtils.getAnnotation(method);
        this.app.make(RequestAttributeRegistry.class)
            .addRegistrar(
                method,
                annotation,
                this.app.make(
                        (Class<RequestAttributeRegistrar>) annotation.get(
                            RequestAttribute.class,
                            "registrar"
                        )
                    )
            );
    }
}
