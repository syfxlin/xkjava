/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.SkipPropertyAutowired;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.PropertyInjector;
import me.ixk.framework.ioc.With;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;

public class DefaultPropertyInjector
    extends AbstractInjector
    implements PropertyInjector {

    public DefaultPropertyInjector(Container container) {
        super(container);
    }

    @Override
    public Object inject(Binding binding, Object instance, With with) {
        if (instance == null) {
            return null;
        }
        Class<?> instanceClass = ClassUtils.getUserClass(instance);
        if (instanceClass.getAnnotation(SkipPropertyAutowired.class) != null) {
            return instance;
        }
        Field[] fields = instanceClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(SkipPropertyAutowired.class) != null) {
                continue;
            }
            Autowired autowired = AnnotationUtils.getAnnotation(
                field,
                Autowired.class
            );
            if (autowired == null) {
                PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                    instanceClass,
                    field.getName()
                );
                if (propertyDescriptor == null) {
                    continue;
                }
                Method writeMethod = propertyDescriptor.getWriteMethod();
                ReflectUtil.invoke(
                    instance,
                    writeMethod,
                    container.getInjectValue(
                        field.getType(),
                        field.getName(),
                        with
                    )
                );
            } else {
                Object dependency;
                if (!autowired.name().equals("")) {
                    dependency =
                        container.make(autowired.name(), field.getType());
                } else {
                    Class<?> autowiredClass;
                    if (autowired.type() == Class.class) {
                        autowiredClass = field.getType();
                    } else {
                        autowiredClass = autowired.type();
                    }
                    dependency =
                        container.getInjectValue(
                            autowiredClass,
                            field.getName(),
                            with
                        );
                }
                ReflectUtil.setFieldValue(instance, field, dependency);
            }
        }
        return instance;
    }
}
