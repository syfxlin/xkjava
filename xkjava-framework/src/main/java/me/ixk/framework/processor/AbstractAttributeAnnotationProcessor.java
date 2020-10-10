/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import me.ixk.framework.annotations.Attribute;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.registrar.AttributeRegistrar;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;

public abstract class AbstractAttributeAnnotationProcessor
    extends AbstractAnnotationProcessor {

    public AbstractAttributeAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        this.processAnnotation(
                Attribute.class,
                this::processAttribute,
                this::processAttribute
            );
    }

    protected abstract void processAttributeItem(
        final AnnotatedElement element,
        final MergedAnnotation attributeAnnotation
    );

    protected void processAttribute(final AnnotatedElement element) {
        final MergedAnnotation attributeAnnotation = AnnotationUtils.getAnnotation(
            element
        );
        this.processAttributeItem(element, attributeAnnotation);
    }

    protected void processAnnotation(
        final AnnotatedElement element,
        final MergedAnnotation attributeAnnotation
    ) {
        ScopeType scoopType = this.getScoopType(attributeAnnotation);
        for (Attribute attribute : attributeAnnotation.getAnnotations(
            Attribute.class
        )) {
            Class<? extends AttributeRegistrar> registrar = attribute.registrar();
            String name = attribute.name();
            if ("".equals(name)) {
                // TODO: custom exception
                throw new RuntimeException("Attribute not set name");
            }
            if (registrar != AttributeRegistrar.class) {
                Object value =
                    this.app.make(registrar)
                        .register(
                            this.app,
                            name,
                            element,
                            scoopType,
                            attributeAnnotation
                        );
                if (value != null) {
                    this.app.setAttribute(name, value, scoopType);
                }
                continue;
            }
            if (element instanceof Class) {
                this.app.setAttribute(
                        name,
                        this.app.make((Class<?>) element),
                        scoopType
                    );
            } else if (element instanceof Method) {
                this.app.setAttribute(
                        name,
                        this.app.call((Method) element),
                        scoopType
                    );
            }
        }
    }
}
