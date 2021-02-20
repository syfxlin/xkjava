/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.ixk.framework.annotation.Autowired;
import me.ixk.framework.annotation.Injector;
import me.ixk.framework.annotation.Order;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.entity.AnnotatedEntry.ChangeableEntry;
import me.ixk.framework.ioc.entity.InjectContext;
import me.ixk.framework.ioc.type.TypeWrapper;
import me.ixk.framework.util.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认字段注入器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:01
 */
@Injector
@Order(Order.LOWEST_PRECEDENCE)
public class DefaultPropertyInjector implements InstanceInjector {

    private static final Logger log = LoggerFactory.getLogger(
        DefaultPropertyInjector.class
    );

    @Override
    public boolean supportsInstance(InjectContext context, Object instance) {
        return context.getFieldEntries().length > 0;
    }

    @Override
    public Object inject(
        Container container,
        Object instance,
        InjectContext context
    ) {
        for (ChangeableEntry<Field> entry : context.getFieldEntries()) {
            if (entry.isChanged()) {
                continue;
            }
            final Field field = entry.getElement();
            final MergedAnnotation annotation = entry.getAnnotation();
            Autowired autowired = annotation.getAnnotation(Autowired.class);
            PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                context.getType(),
                field.getName()
            );
            Method writeMethod = propertyDescriptor == null
                ? null
                : propertyDescriptor.getWriteMethod();
            if (autowired == null) {
                if (writeMethod == null) {
                    continue;
                }
                Object dependency = context
                    .getBinder()
                    .getObject(
                        field.getName(),
                        TypeWrapper.forField(field),
                        annotation
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
                    TypeWrapper<?> typeWrapper;
                    if (type == Class.class) {
                        typeWrapper =
                            TypeWrapper.forField(field, autowired.proxyType());
                    } else {
                        typeWrapper =
                            TypeWrapper.forClass(type, autowired.proxyType());
                    }
                    dependency =
                        context
                            .getBinder()
                            .getObject(
                                field.getName(),
                                typeWrapper,
                                annotation
                            );
                }
                if (dependency == null) {
                    dependency = ReflectUtil.getFieldValue(instance, field);
                }
                // 如果必须注入，但是为 null，则抛出错误
                if (dependency == null && autowired.required()) {
                    final NullPointerException exception = new NullPointerException(
                        "Target [" +
                        context.getType().getName() +
                        "::" +
                        field.getName() +
                        "] is required, but inject value is null"
                    );
                    log.error(
                        "Target [{}::{}] is required, but inject value is null",
                        context.getType().getName(),
                        field.getName()
                    );
                    throw exception;
                }
                if (writeMethod == null) {
                    ReflectUtil.setFieldValue(instance, field, dependency);
                } else {
                    ReflectUtil.invoke(instance, writeMethod, dependency);
                }
            }
            entry.setChanged(true);
        }
        return instance;
    }
}
