/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Attribute;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 前置注解处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:50
 */
@AnnotationProcessor
@Order(Order.MEDIUM_PRECEDENCE - 1)
public class BeforeAttributeAnnotationProcessor
    extends AbstractAttributeAnnotationProcessor {

    public BeforeAttributeAnnotationProcessor(final XkJava app) {
        super(app);
    }

    @Override
    protected void processAttributeItem(
        AnnotatedElement element,
        MergedAnnotation attributeAnnotation
    ) {
        final Boolean after = attributeAnnotation.get(
            Attribute.class,
            "after",
            Boolean.class
        );
        if (after != null && !after) {
            this.processAnnotation(element, attributeAnnotation);
        }
    }
}
