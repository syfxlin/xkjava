/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import static me.ixk.framework.util.DataUtils.caseGet;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import me.ixk.framework.annotation.core.ConfigurationProperties;
import me.ixk.framework.annotation.core.Injector;
import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.annotation.core.PropertyValue;
import me.ixk.framework.annotation.core.Value;
import me.ixk.framework.bootstrap.Bootstrap;
import me.ixk.framework.expression.BeanExpressionResolver;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.PropertyResolver;
import me.ixk.framework.ioc.entity.AnnotatedEntry.ChangeableEntry;
import me.ixk.framework.ioc.entity.ClassProperty;
import me.ixk.framework.ioc.entity.InjectContext;
import me.ixk.framework.ioc.processor.PropertiesProcessor;
import me.ixk.framework.property.CompositePropertySource;
import me.ixk.framework.property.Environment;
import me.ixk.framework.util.Convert;
import me.ixk.framework.util.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties 值默认注入器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:04
 */
@Injector
@Order(Order.HIGHEST_PRECEDENCE)
public class PropertiesValueInjector implements InstanceInjector {

    private static final Logger log = LoggerFactory.getLogger(
        PropertiesValueInjector.class
    );

    @Override
    public boolean supportsInstance(
        final InjectContext context,
        final Object instance
    ) {
        if (
            instance == null ||
            instance instanceof Bootstrap ||
            instance instanceof Environment
        ) {
            return false;
        }
        return context.getFieldEntries().length > 0;
    }

    @Override
    public Object inject(
        final Container container,
        final Object instance,
        final InjectContext context
    ) {
        for (final ChangeableEntry<Field> entry : context.getFieldEntries()) {
            if (entry.isChanged()) {
                continue;
            }
            final Field field = entry.getElement();
            final MergedAnnotation fieldAnnotation = entry.getAnnotation();
            final boolean hasConfig = context
                .getAnnotation()
                .hasAnnotation(ConfigurationProperties.class);
            final boolean hasValue = fieldAnnotation.hasAnnotation(Value.class);
            // 不存在 @Value 或者 @Configuration 注解的时候则无需注入
            if (!hasConfig && !hasValue) {
                continue;
            }
            final PropertyValue propertyValue = fieldAnnotation.getAnnotation(
                PropertyValue.class
            );
            // 手动配置跳过
            if (propertyValue != null && propertyValue.skip()) {
                continue;
            }
            final PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                context.getType(),
                field.getName()
            );
            final Method writeMethod = propertyDescriptor == null
                ? null
                : propertyDescriptor.getWriteMethod();
            final ClassProperty property = new ClassProperty(
                instance,
                context.getType(),
                field,
                field.getName(),
                context.getAnnotation(),
                fieldAnnotation
            );
            final Object value =
                this.resolveValue(
                        property,
                        context.getData(PropertiesProcessor.PROPERTIES),
                        context.getData(PropertiesProcessor.PROPERTIES_PREFIX),
                        container
                    );
            // 有 Write 方法就使用 Write 方法
            if (writeMethod != null) {
                ReflectUtil.invoke(instance, writeMethod, value);
            } else {
                ReflectUtil.setFieldValue(instance, field, value);
            }
            entry.setChanged(true);
        }
        return instance;
    }

    private Object resolveValue(
        final ClassProperty property,
        CompositePropertySource compositePropertySource,
        String prefix,
        final Container container
    ) {
        final MergedAnnotation classAnnotation = property.getClassAnnotation();
        final ConfigurationProperties configurationProperties = classAnnotation.getAnnotation(
            ConfigurationProperties.class
        );
        final String propertyName = property.getPropertyName();
        Object value =
            this.getValue(property, compositePropertySource, prefix, container);
        if (
            value == null &&
            !(
                configurationProperties == null ||
                configurationProperties.ignoreUnknownFields()
            )
        ) {
            final NullPointerException exception = new NullPointerException(
                "Unknown property [" + prefix + "." + propertyName + "]"
            );
            log.error("Unknown property [{}.{}]", prefix, propertyName);
            throw exception;
        }
        try {
            value = Convert.convert(property.getPropertyType(), value);
        } catch (final UtilException e) {
            if (
                !(
                    configurationProperties == null ||
                    configurationProperties.ignoreInvalidFields()
                )
            ) {
                final RuntimeException exception = new RuntimeException(
                    "Invalid property [" + prefix + "." + propertyName + "]",
                    e
                );
                log.error("Invalid property [{}.{}]", prefix, propertyName);
                throw exception;
            }
            value = ClassUtil.getDefaultValue(property.getPropertyType());
        }
        return value;
    }

    private Object getValue(
        final ClassProperty property,
        final CompositePropertySource properties,
        final String prefix,
        final Container container
    ) {
        final MergedAnnotation propertyAnnotation = property.getPropertyAnnotation();
        // 有 @Value 就优先使用
        final Value value = propertyAnnotation.getAnnotation(Value.class);
        if (value != null) {
            return this.resolveExpression(value, properties, prefix, container);
        }
        final PropertyValue propertyValue = propertyAnnotation.getAnnotation(
            PropertyValue.class
        );
        if (propertyValue != null) {
            return this.resolvePropertyValue(
                    propertyValue,
                    properties,
                    prefix,
                    container,
                    property
                );
        }
        Object result = caseGet(property.getPropertyName(), properties::get);
        if (result == null && prefix != null && !prefix.isEmpty()) {
            result =
                caseGet(prefix + property.getPropertyName(), properties::get);
        }
        return result;
    }

    private Object resolveExpression(
        final Value value,
        final me.ixk.framework.property.PropertySource<?> properties,
        final String prefix,
        final Container container
    ) {
        final BeanExpressionResolver resolver = container.make(
            BeanExpressionResolver.class
        );
        return resolver.evaluateResolver(
            value.expression(),
            Object.class,
            properties,
            Collections.emptyMap(),
            name ->
                BeanExpressionResolver.resolveEmbeddedValue(
                    name,
                    properties,
                    prefix
                )
        );
    }

    private Object resolvePropertyValue(
        final PropertyValue value,
        final me.ixk.framework.property.PropertySource<?> properties,
        final String prefix,
        final Container container,
        final ClassProperty property
    ) {
        if (value.skip()) {
            return null;
        }
        Object result = caseGet(value.name(), properties::get);
        if (result == null && prefix != null && !prefix.isEmpty()) {
            result = caseGet(prefix + value.name(), properties::get);
        }
        if (
            result == null && !PropertyValue.EMPTY.equals(value.defaultValue())
        ) {
            result = value.defaultValue();
        }
        if (value.resolver() != PropertyResolver.class) {
            final PropertyResolver resolver = container.make(value.resolver());
            if (resolver.supportsProperty((String) result, property)) {
                result = resolver.resolveProperty((String) result, property);
            }
        }
        return result;
    }
}
