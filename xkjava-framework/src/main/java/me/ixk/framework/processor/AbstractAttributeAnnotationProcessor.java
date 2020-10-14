/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import me.ixk.framework.annotations.Attribute;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.registry.attribute.AttributeRegistry;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 属性注解处理器（抽象类）
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:46
 */
public abstract class AbstractAttributeAnnotationProcessor
    extends AbstractAnnotationProcessor {
    private static final Logger log = LoggerFactory.getLogger(
        AbstractAttributeAnnotationProcessor.class
    );

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

    /**
     * 处理单个属性
     *
     * @param element             注解元素
     * @param attributeAnnotation 组合注解
     */
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
            Class<? extends AttributeRegistry> registry = attribute.registry();
            String name = attribute.name();
            if ("".equals(name)) {
                log.error("Attribute note set name: {}", element);
                throw new AnnotationProcessorException(
                    "Attribute not set name"
                );
            }
            if (registry != AttributeRegistry.class) {
                Object value =
                    this.app.make(registry)
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
