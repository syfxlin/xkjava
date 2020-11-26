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
import me.ixk.framework.annotations.EnvValue;
import me.ixk.framework.annotations.Expression;
import me.ixk.framework.annotations.Injector;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.bootstrap.Bootstrap;
import me.ixk.framework.config.ClassProperty;
import me.ixk.framework.config.PropertyResolver;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.InjectorEntry;
import me.ixk.framework.ioc.InstanceContext;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.kernel.Environment;
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
@Injector
@Order(Order.HIGHEST_PRECEDENCE)
public class PropertiesValueInjector implements InstanceInjector {
    private static final Logger log = LoggerFactory.getLogger(
        PropertiesValueInjector.class
    );

    protected Object injectConfigurationProperties(
        Container container,
        Environment environment,
        ClassProperty property,
        ConfigurationProperties configAnnotation,
        EnvValue envValueAnnotation,
        Map<String, Object> properties
    ) {
        String fieldName = property.getPropertyName();
        Object value;
        if (envValueAnnotation == null) {
            value = caseGet(fieldName, properties::get);
        } else {
            value =
                caseGet(
                    fieldName,
                    envValueAnnotation.full()
                        ? environment::get
                        : properties::get
                );
            if (value == null) {
                final String defaultValue = envValueAnnotation.defaultValue();
                value =
                    EnvValue.EMPTY.equals(defaultValue) ? null : defaultValue;
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
                envValueAnnotation != null &&
                envValueAnnotation.resolver() != PropertyResolver.class
            ) {
                final PropertyResolver resolver = container.make(
                    envValueAnnotation.resolver()
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

    protected Object injectExpression(final Expression expression) {
        return Express.evaluateApp(expression.expression(), Object.class);
    }

    @Override
    public boolean supportsInstance(InstanceContext context, Object instance) {
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
        Container container,
        Object instance,
        InstanceContext context,
        DataBinder dataBinder
    ) {
        final Environment environment = container.make(Environment.class);
        Map<String, Object> prefixProps = null;
        final ConfigurationProperties configAnnotation = context
            .getAnnotation()
            .getAnnotation(ConfigurationProperties.class);
        // 存在 @ConfigurationProperties 注解的时候就获取配置中所有 prefix 的值
        if (configAnnotation != null) {
            prefixProps = environment.getPrefix(configAnnotation.prefix());
        }
        for (final InjectorEntry<Field> entry : context.getFieldEntries()) {
            if (entry.isChanged()) {
                continue;
            }
            final Field field = entry.getElement();
            final MergedAnnotation fieldAnnotation = entry.getAnnotation();
            final Expression expressionAnnotation = fieldAnnotation.getAnnotation(
                Expression.class
            );
            // 不存在 @Value 或者 @Configuration 注解的时候则无需注入
            if (configAnnotation == null && expressionAnnotation == null) {
                continue;
            }
            final PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                context.getInstanceType(),
                field.getName()
            );
            final Method writeMethod = propertyDescriptor == null
                ? null
                : propertyDescriptor.getWriteMethod();
            final Object value;
            if (configAnnotation != null && expressionAnnotation == null) {
                // @ConfigurationProperties 没有 @Value 注解
                final EnvValue envValueAnnotation = fieldAnnotation.getAnnotation(
                    EnvValue.class
                );
                // @EnvValue 配置了 skip 值，则跳过
                if (envValueAnnotation != null && envValueAnnotation.skip()) {
                    continue;
                }
                String fieldName = envValueAnnotation == null
                    ? null
                    : envValueAnnotation.name();
                if (fieldName == null || fieldName.isEmpty()) {
                    // 若为配置值则使用属性名
                    fieldName = field.getName();
                }
                ClassProperty property = new ClassProperty(
                    instance,
                    context.getInstanceType(),
                    field,
                    fieldName,
                    context.getAnnotation(),
                    fieldAnnotation
                );
                value =
                    this.injectConfigurationProperties(
                            container,
                            environment,
                            property,
                            configAnnotation,
                            envValueAnnotation,
                            prefixProps
                        );
            } else {
                // 有 @Value 注解就优先使用
                value = this.injectExpression(expressionAnnotation);
            }
            if (writeMethod != null) {
                ReflectUtil.invoke(instance, writeMethod, value);
            } else {
                ReflectUtil.setFieldValue(instance, field, value);
            }
            entry.setChanged(true);
        }
        return instance;
    }
}
