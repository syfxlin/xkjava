/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import static me.ixk.framework.helpers.Util.caseGet;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import me.ixk.framework.annotations.ConfigurationProperties;
import me.ixk.framework.annotations.Property;
import me.ixk.framework.annotations.Value;
import me.ixk.framework.bootstrap.Bootstrap;
import me.ixk.framework.config.ClassProperty;
import me.ixk.framework.config.PropertyResolver;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.Express;
import me.ixk.framework.utils.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties 值默认注入器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:04
 */
public class PropertiesValueInjector implements InstanceInjector {
    private static final Logger log = LoggerFactory.getLogger(
        PropertiesValueInjector.class
    );

    @Override
    public Object inject(
        final Container container,
        final Binding binding,
        final Object instance,
        final Class<?> instanceClass,
        final DataBinder dataBinder
    ) {
        if (
            instance == null ||
            instance instanceof Bootstrap ||
            instance instanceof Environment ||
            AnnotationUtils.isSkipped(instanceClass, this.getClass())
        ) {
            return instance;
        }
        final Field[] fields = instanceClass.getDeclaredFields();
        final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
            instanceClass
        );
        final Environment environment = container.make(Environment.class);
        Map<String, Object> prefixProps = null;
        final ConfigurationProperties configAnnotation = annotation.getAnnotation(
            ConfigurationProperties.class
        );
        // 存在 @ConfigurationProperties 注解的时候就获取配置中所有 prefix 的值
        if (configAnnotation != null) {
            prefixProps = environment.getPrefix(configAnnotation.prefix());
        }
        for (final Field field : fields) {
            // 若使用了 @Skip 注解则跳过
            if (AnnotationUtils.isSkipped(field, this.getClass())) {
                continue;
            }
            final MergedAnnotation fieldAnnotation = AnnotationUtils.getAnnotation(
                field
            );
            final Value valueAnnotation = fieldAnnotation.getAnnotation(
                Value.class
            );
            // 不存在 @Value 或者 @Configuration 注解的时候则无需注入
            if (configAnnotation == null && valueAnnotation == null) {
                continue;
            }
            final PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                instanceClass,
                field.getName()
            );
            final Method writeMethod = propertyDescriptor == null
                ? null
                : propertyDescriptor.getWriteMethod();
            final Object value;
            if (configAnnotation != null && valueAnnotation == null) {
                // @ConfigurationProperties 没有 @Value 注解
                final Property propertyAnnotation = fieldAnnotation.getAnnotation(
                    Property.class
                );
                // @Property 配置了 skip 值，则跳过
                if (propertyAnnotation != null && propertyAnnotation.skip()) {
                    continue;
                }
                String fieldName = propertyAnnotation == null
                    ? null
                    : propertyAnnotation.name();
                if (fieldName == null || fieldName.isEmpty()) {
                    // 若为配置值则使用属性名
                    fieldName = field.getName();
                }
                ClassProperty property = new ClassProperty(
                    instance,
                    instanceClass,
                    field,
                    fieldName,
                    annotation,
                    fieldAnnotation
                );
                value =
                    this.injectConfigurationProperties(
                            container,
                            environment,
                            property,
                            configAnnotation,
                            propertyAnnotation,
                            prefixProps
                        );
            } else {
                // 有 @Value 注解就优先使用
                value = this.injectValue(valueAnnotation);
            }
            if (writeMethod != null) {
                ReflectUtil.invoke(instance, writeMethod, value);
            } else {
                ReflectUtil.setFieldValue(instance, field, value);
            }
        }
        return instance;
    }

    protected Object injectConfigurationProperties(
        Container container,
        Environment environment,
        ClassProperty property,
        ConfigurationProperties configAnnotation,
        Property propertyAnnotation,
        Map<String, Object> properties
    ) {
        String fieldName = property.getPropertyName();
        Object value;
        if (propertyAnnotation == null) {
            value = caseGet(fieldName, properties::get);
        } else {
            value =
                caseGet(
                    fieldName,
                    propertyAnnotation.full()
                        ? environment::get
                        : properties::get
                );
            if (value == null) {
                final String defaultValue = propertyAnnotation.defaultValue();
                value =
                    Property.EMPTY.equals(defaultValue) ? null : defaultValue;
            }
        }
        if (value == null && !configAnnotation.ignoreUnknownFields()) {
            final NullPointerException exception = new NullPointerException(
                "Unknown property [" +
                configAnnotation.prefix() +
                "." +
                fieldName +
                "]"
            );
            log.error(
                "Unknown property [{}.{}]",
                configAnnotation.prefix(),
                fieldName
            );
            throw exception;
        }
        try {
            if (
                propertyAnnotation != null &&
                propertyAnnotation.resolver() != PropertyResolver.class
            ) {
                final PropertyResolver resolver = container.make(
                    propertyAnnotation.resolver()
                );
                if (resolver.supportsProperty((String) value, property)) {
                    value = resolver.resolveProperty((String) value, property);
                }
            }
            value = Convert.convert(property.getPropertyType(), value);
        } catch (Exception e) {
            if (!configAnnotation.ignoreInvalidFields()) {
                final RuntimeException exception = new RuntimeException(
                    "Invalid property [" +
                    configAnnotation.prefix() +
                    "." +
                    fieldName +
                    "]",
                    e
                );
                log.error(
                    "Invalid property [{}.{}]",
                    configAnnotation.prefix(),
                    fieldName
                );
                throw exception;
            }
            value = ClassUtil.getDefaultValue(property.getPropertyType());
        }
        return value;
    }

    protected Object injectValue(final Value value) {
        return Express.evaluateApp(value.value(), Object.class);
    }
}
