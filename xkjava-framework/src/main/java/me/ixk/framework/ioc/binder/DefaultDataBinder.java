/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.binder;

import cn.hutool.core.convert.Convert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ixk.framework.annotation.DataBind;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.type.TypeWrapper;
import me.ixk.framework.util.ClassUtils;
import me.ixk.framework.util.MergedAnnotation;

/**
 * 默认数据绑定器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:38
 */
public class DefaultDataBinder implements DataBinder {

    private final Map<String, Object> objects = new HashMap<>();
    private final Map<Class<?>, List<String>> objectTypes = new HashMap<>();

    public DefaultDataBinder() {}

    public DefaultDataBinder(final Map<String, Object> objects) {
        objects.forEach(this::addType);
    }

    private void addType(String name, Object instance) {
        this.objects.put(name, instance);
        Class<?> type = ClassUtils.getUserClass(instance);
        while (type != null && !ClassUtils.isSkipBuildType(type)) {
            this.addTypeToMap(name, type);
            for (final Class<?> in : type.getInterfaces()) {
                this.addTypeToMap(name, in);
            }
            type = type.getSuperclass();
        }
    }

    private void addTypeToMap(String name, Class<?> type) {
        this.objectTypes.compute(
                type,
                (t, list) -> {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(name);
                    return list;
                }
            );
    }

    @Override
    public <T> T getObject(
        String name,
        final TypeWrapper<T> type,
        final MergedAnnotation annotation,
        final Container container
    ) {
        final DataBind dataBind = annotation == null
            ? null
            : annotation.getAnnotation(DataBind.class);
        if (dataBind != null && dataBind.name().length() != 0) {
            name = dataBind.name();
        }
        final Class<T> clazz = type.getClazz();
        // name equals
        Object object = this.objects.get(name);
        // Disable name equals but type not instanceof
        if (object != null && !clazz.isInstance(object)) {
            object = null;
        }
        if (object == null) {
            // type equals
            final List<String> list = this.objectTypes.get(clazz);
            final String objectName;
            if (list == null || list.isEmpty()) {
                objectName = container.typeToBeanName(clazz);
            } else {
                objectName = list.get(0);
            }
            object = this.objects.get(objectName);
        }
        if (object == null) {
            object = container.make(name, type, this);
        }
        if (
            object == null &&
            dataBind != null &&
            DataBind.EMPTY.equals(dataBind.defaultValue())
        ) {
            object = dataBind.defaultValue();
        }
        return Convert.convert(clazz, object);
    }

    public Map<String, Object> getObjects() {
        return objects;
    }

    public Map<Class<?>, List<String>> getObjectTypes() {
        return objectTypes;
    }

    public DefaultDataBinder add(final String name, final Object object) {
        this.addType(name, object);
        return this;
    }

    public DefaultDataBinder remove(final String name) {
        final Object remove = this.objects.remove(name);
        if (remove == null) {
            return this;
        }
        this.objectTypes.forEach((k, v) -> v.remove(name));
        return this;
    }
}
