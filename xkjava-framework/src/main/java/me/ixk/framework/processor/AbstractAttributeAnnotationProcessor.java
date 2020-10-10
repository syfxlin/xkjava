/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import me.ixk.framework.annotations.Attribute;
import me.ixk.framework.annotations.Attributes;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.registrar.AttributeRegistrar;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergeAnnotation;

public abstract class AbstractAttributeAnnotationProcessor
    extends AbstractAnnotationProcessor {

    public AbstractAttributeAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        this.processAnnotation(
                Attributes.class,
                this::processAttributes,
                this::processAttributes
            );
        this.processAnnotation(
                Attribute.class,
                this::processAttribute,
                this::processAttribute
            );
    }

    protected abstract void processAttributeItem(
        final AnnotatedElement element,
        final MergeAnnotation attributeAnnotation
    );

    protected void processAttributes(final AnnotatedElement element) {
        final MergeAnnotation attributesAnnotation = AnnotationUtils.getAnnotation(
            element,
            Attributes.class
        );
        for (Attribute attribute : (Attribute[]) attributesAnnotation.get(
            Attributes.class,
            "value"
        )) {
            MergeAnnotation clone = AnnotationUtils.cloneAnnotation(
                attributesAnnotation
            );
            clone.addAnnotation(attribute);
            this.processAttributeItem(element, clone);
        }
    }

    protected void processAttribute(final AnnotatedElement element) {
        final MergeAnnotation attributeAnnotation = AnnotationUtils.getAnnotation(
            element,
            Attribute.class
        );
        this.processAttributeItem(element, attributeAnnotation);
    }

    protected void processAnnotation(
        final AnnotatedElement element,
        final MergeAnnotation attributeAnnotation
    ) {
        ScopeType scoopType = this.getScoopType(element);
        Class<AttributeRegistrar> registrar = attributeAnnotation.get(
            Attribute.class,
            "registrar"
        );
        String name = attributeAnnotation.get(Attribute.class, "name");
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
            return;
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
