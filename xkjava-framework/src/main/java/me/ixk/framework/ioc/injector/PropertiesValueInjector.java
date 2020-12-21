/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import static me.ixk.framework.utils.DataUtils.caseGet;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ixk.framework.annotations.ConfigurationProperties;
import me.ixk.framework.annotations.Injector;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.PropertySource;
import me.ixk.framework.annotations.PropertyValue;
import me.ixk.framework.annotations.Value;
import me.ixk.framework.bootstrap.Bootstrap;
import me.ixk.framework.config.ClassProperty;
import me.ixk.framework.exceptions.ContainerException;
import me.ixk.framework.expression.BeanExpressionResolver;
import me.ixk.framework.ioc.AnnotatedEntry.ChangeableEntry;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.InstanceContext;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.ioc.PropertyResolver;
import me.ixk.framework.property.CompositePropertySource;
import me.ixk.framework.property.Environment;
import me.ixk.framework.property.MapPropertySource;
import me.ixk.framework.property.PropertiesPropertySource;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.utils.ResourceUtils;
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
    protected static final String PROPERTIES_SPLIT = ".";

    @Override
    public boolean supportsInstance(
        final InstanceContext context,
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
        final InstanceContext context,
        final DataBinder dataBinder
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
                context.getInstanceType(),
                field.getName()
            );
            final Method writeMethod = propertyDescriptor == null
                ? null
                : propertyDescriptor.getWriteMethod();
            final ClassProperty property = new ClassProperty(
                instance,
                context.getInstanceType(),
                field,
                field.getName(),
                context.getAnnotation(),
                fieldAnnotation
            );
            final Object value = this.resolveValue(property, container);
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
        final Container container
    ) {
        final MergedAnnotation classAnnotation = property.getClassAnnotation();
        final ConfigurationProperties configurationProperties = classAnnotation.getAnnotation(
            ConfigurationProperties.class
        );
        final List<PropertySource> propertySources = classAnnotation.getAnnotations(
            PropertySource.class
        );
        final CompositePropertySource compositePropertySource = new CompositePropertySource(
            property.getInstanceClass().getName()
        );
        compositePropertySource.setPropertySource(
            container.make(Environment.class)
        );
        if (!propertySources.isEmpty()) {
            for (final PropertySource propertySource : propertySources) {
                this.loadPropertySource(
                        compositePropertySource,
                        propertySource
                    );
            }
        }
        String prefix = configurationProperties == null
            ? ""
            : configurationProperties.prefix();
        if (!prefix.isEmpty() && !prefix.endsWith(PROPERTIES_SPLIT)) {
            prefix += PROPERTIES_SPLIT;
        }
        final String propertyName = property.getPropertyName();
        Object value =
            this.resolveValue(
                    property,
                    compositePropertySource,
                    prefix,
                    container
                );
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

    private void loadPropertySource(
        final CompositePropertySource compositePropertySource,
        final PropertySource propertySource
    ) {
        final String name = propertySource.location();
        if (!name.isEmpty()) {
            try {
                final File file = ResourceUtils.getFile(name);
                final String encoding = propertySource.encoding();
                compositePropertySource.setPropertySource(
                    new PropertiesPropertySource(
                        compositePropertySource.getName() + ":" + name,
                        file,
                        encoding
                    )
                );
            } catch (final FileNotFoundException e) {
                if (!propertySource.ignoreResourceNotFound()) {
                    throw new ContainerException(e);
                }
            }
        }
        if (propertySource.value().length > 0) {
            Map<String, Object> map = new HashMap<>(
                propertySource.value().length
            );
            for (final String value : propertySource.value()) {
                final String[] kv = value.split("=");
                map.put(kv[0], kv.length > 1 ? kv[1] : "");
            }
            compositePropertySource.setPropertySource(
                new MapPropertySource<>(
                    compositePropertySource.getName() + ":inline",
                    map
                )
            );
        }
    }

    private Object resolveValue(
        final ClassProperty property,
        final me.ixk.framework.property.PropertySource<?> properties,
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
