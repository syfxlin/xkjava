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
import me.ixk.framework.annotations.Value;
import me.ixk.framework.bootstrap.Bootstrap;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.Express;
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
        Container container,
        Binding binding,
        Object instance,
        Class<?> instanceClass,
        DataBinder dataBinder
    ) {
        if (
            instance == null ||
            instance instanceof Bootstrap ||
            instance instanceof Environment
        ) {
            return instance;
        }
        Field[] fields = instanceClass.getDeclaredFields();
        ConfigurationProperties config = AnnotationUtils
            .getAnnotation(instanceClass)
            .getAnnotation(ConfigurationProperties.class);
        Environment environment = container.make(Environment.class);
        Map<String, Object> prefixProps = null;
        if (config != null) {
            prefixProps = environment.getPrefix(config.prefix());
        }
        for (Field field : fields) {
            Value valueAnnotation = field.getAnnotation(Value.class);
            if (config == null && valueAnnotation == null) {
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
            if (config != null && valueAnnotation == null) {
                // @ConfigurationProperties 没有 @Value 注解
                value =
                    this.injectConfigurationProperties(
                            field,
                            config,
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
        Field field,
        ConfigurationProperties config,
        Map<String, Object> properties
    ) {
        Object value = caseGet(field.getName(), properties::get);
        if (value == null && !((boolean) config.ignoreUnknownFields())) {
            final NullPointerException exception = new NullPointerException(
                "Unknown property [" +
                config.prefix() +
                "." +
                field.getName() +
                "]"
            );
            log.error(
                "Unknown property [{}.{}]",
                config.prefix(),
                field.getName()
            );
            throw exception;
        }
        try {
            value = Convert.convert(field.getType(), value);
        } catch (Exception e) {
            if (!((boolean) config.ignoreInvalidFields())) {
                final RuntimeException exception = new RuntimeException(
                    "Invalid property [" +
                    config.prefix() +
                    "." +
                    field.getName() +
                    "]",
                    e
                );
                log.error(
                    "Invalid property [{}.{}]",
                    config.prefix(),
                    field.getName()
                );
                throw exception;
            }
            value = ClassUtil.getDefaultValue(field.getType());
        }
        return value;
    }

    protected Object injectValue(Value value) {
        return Express.evaluateApp(value.value(), Object.class);
    }
}
