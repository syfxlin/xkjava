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
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.utils.AnnotationUtils;

public class DefaultPropertyInjector implements InstanceInjector {

    @Override
    public Object inject(
        Container container,
        Binding binding,
        Object instance,
        Class<?> instanceClass,
        DataBinder dataBinder
    ) {
        if (instanceClass.getAnnotation(SkipPropertyAutowired.class) != null) {
            return instance;
        }
        Field[] fields = instanceClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(SkipPropertyAutowired.class) != null) {
                continue;
            }
            Autowired autowired = AnnotationUtils
                .getAnnotation(field)
                .getAnnotation(Autowired.class);
            if (autowired == null) {
                PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                    instanceClass,
                    field.getName()
                );
                if (propertyDescriptor == null) {
                    continue;
                }
                Method writeMethod = propertyDescriptor.getWriteMethod();
                if (writeMethod == null) {
                    continue;
                }
                Object dependency = dataBinder.getObject(
                    field.getName(),
                    field.getType()
                );
                if (dependency == null) {
                    dependency = ReflectUtil.getFieldValue(instance, field);
                }
                ReflectUtil.invoke(instance, writeMethod, dependency);
            } else {
                Object dependency;
                String name = autowired.name();
                Class<?> type = autowired.type();
                if (!"".equals(name)) {
                    dependency = container.make(name, field.getType());
                } else {
                    Class<?> autowiredClass;
                    if (type == Class.class) {
                        autowiredClass = field.getType();
                    } else {
                        autowiredClass = type;
                    }
                    dependency =
                        dataBinder.getObject(field.getName(), autowiredClass);
                }
                if (dependency == null) {
                    dependency = ReflectUtil.getFieldValue(instance, field);
                }
                // 如果必须注入，但是为 null，则抛出错误
                if (dependency == null && (boolean) autowired.required()) {
                    throw new NullPointerException(
                        "Target [" +
                        instanceClass.getName() +
                        "::" +
                        field.getName() +
                        "] is required, but inject value is null"
                    );
                }
                ReflectUtil.setFieldValue(instance, field, dependency);
            }
        }
        return instance;
    }
}
