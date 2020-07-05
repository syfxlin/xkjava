/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import me.ixk.framework.annotations.ConfigurationProperties;
import me.ixk.framework.annotations.Value;
import me.ixk.framework.bootstrap.Bootstrap;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.ioc.With;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;
import me.ixk.framework.utils.Express;

public class PropertiesValueInjector implements InstanceInjector {

    @Override
    public Object inject(
        Container container,
        Binding binding,
        Object instance,
        With with
    ) {
        if (
            instance == null ||
            instance instanceof Bootstrap ||
            instance instanceof Environment
        ) {
            return instance;
        }
        Class<?> instanceClass = ClassUtils.getUserClass(instance);
        Field[] fields = instanceClass.getDeclaredFields();
        ConfigurationProperties config = AnnotationUtils.getAnnotation(
            instanceClass,
            ConfigurationProperties.class
        );
        Environment environment = container.make(Environment.class);
        Map<String, Object> prefixProps = null;
        if (config != null) {
            prefixProps = environment.getPrefix(config.prefix());
        }
        for (Field field : fields) {
            Value valueAnno = field.getAnnotation(Value.class);
            if (config == null && valueAnno == null) {
                continue;
            }
            PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                instanceClass,
                field.getName()
            );
            if (propertyDescriptor == null) {
                continue;
            }
            Method writeMethod = propertyDescriptor.getWriteMethod();
            Object value;
            if (config != null && valueAnno == null) {
                // @ConfigurationProperties 没有 @Value 注解
                value =
                    this.injectConfigurationProperties(
                            field,
                            config,
                            prefixProps
                        );
            } else {
                // 有 @Value 注解就优先使用
                value = this.injectValue(field, valueAnno);
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
        Field field,
        ConfigurationProperties config,
        Map<String, Object> properties
    ) {
        Object value = properties.get(field.getName());
        if (value == null && !config.ignoreUnknownFields()) {
            throw new NullPointerException(
                "Unknown property [" +
                config.prefix() +
                "." +
                field.getName() +
                "]"
            );
        }
        try {
            value = Convert.convert(field.getType(), value);
        } catch (Exception e) {
            if (!config.ignoreInvalidFields()) {
                throw new RuntimeException(
                    "Invalid property [" +
                    config.prefix() +
                    "." +
                    field.getName() +
                    "]",
                    e
                );
            }
            value = ClassUtil.getDefaultValue(field.getType());
        }
        return value;
    }

    protected Object injectValue(Field field, Value value) {
        return Express.executeEnv(value.value());
    }
}
