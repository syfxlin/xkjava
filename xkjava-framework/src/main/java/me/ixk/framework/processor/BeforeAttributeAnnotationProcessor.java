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

@AnnotationProcessor
@Order(Order.MEDIUM_PRECEDENCE - 1)
public class BeforeAttributeAnnotationProcessor
    extends AbstractAttributeAnnotationProcessor {

    public BeforeAttributeAnnotationProcessor(final XkJava app) {
        super(app);
    }

    protected void processAttributeItem(
        AnnotatedElement element,
        MergedAnnotation attributeAnnotation
    ) {
        if (!((Boolean) attributeAnnotation.get(Attribute.class, "after"))) {
            this.processAnnotation(element, attributeAnnotation);
        }
    }
}
