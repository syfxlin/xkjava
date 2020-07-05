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
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.ioc.With;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;

public class ConfigurationPropertiesInjector implements InstanceInjector {

    @Override
    public Object inject(
        Container container,
        Binding binding,
        Object instance,
        With with
    ) {
        if (instance == null) {
            return null;
        }
        Class<?> instanceClass = ClassUtils.getUserClass(instance);
        ConfigurationProperties config = AnnotationUtils.getAnnotation(
            instanceClass,
            ConfigurationProperties.class
        );
        if (config == null) {
            return instance;
        }
        Map<String, Object> properties = container
            .make(Environment.class)
            .getPrefix(config.prefix());
        Field[] fields = instanceClass.getDeclaredFields();
        for (Field field : fields) {
            PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                instanceClass,
                field.getName()
            );
            if (propertyDescriptor == null) {
                continue;
            }
            Method writeMethod = propertyDescriptor.getWriteMethod();
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
            if (writeMethod != null) {
                ReflectUtil.invoke(instance, writeMethod, value);
            } else {
                boolean canAccess = field.canAccess(instance);
                field.setAccessible(true);
                ReflectUtil.setFieldValue(instance, field, value);
                field.setAccessible(canAccess);
            }
        }
        return instance;
    }
}
