/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.binder;

import cn.hutool.core.convert.Convert;
import java.util.Map;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.type.TypeWrapper;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 默认数据绑定器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:38
 */
public class DefaultDataBinder implements DataBinder {

    private final Container container;

    private final Map<String, Object> data;

    public DefaultDataBinder(Container container, Map<String, Object> data) {
        this.container = container;
        this.data = data;
    }

    @Override
    public <T> T getObject(
        String name,
        TypeWrapper<T> type,
        MergedAnnotation annotation
    ) {
        DataBind dataBind = annotation == null
            ? null
            : annotation.getAnnotation(DataBind.class);
        if (dataBind != null && dataBind.name().length() != 0) {
            name = dataBind.name();
        }
        Object object = this.data.get(name);
        if (object == null) {
            object = this.data.get(type.getClazz().getName());
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
        return Convert.convert(type.getClazz(), object);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public DefaultDataBinder add(String name, Object object) {
        this.data.put(name, object);
        return this;
    }

    public DefaultDataBinder remove(String name) {
        this.data.remove(name);
        return this;
    }
}
