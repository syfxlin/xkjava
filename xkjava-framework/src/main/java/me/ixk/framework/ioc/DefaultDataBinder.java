/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Map;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.MergeAnnotation;

public class DefaultDataBinder implements DataBinder {
    private final Container container;

    private final Map<String, Object> data;

    public DefaultDataBinder(Container container, Map<String, Object> data) {
        this.container = container;
        this.data = data;
    }

    @Override
    public <T> T getObject(String name, Class<T> type) {
        Object object = this.data.get(name);
        if (object == null) {
            object = this.data.get(type.getName());
        }
        if (object == null) {
            object = container.make(name, type, this);
        }
        if (object == null) {
            object = container.make(type, this);
        }
        return Convert.convert(type, object);
    }

    @Override
    public <T> T getObject(
        String name,
        Class<T> type,
        MergeAnnotation dataBind
    ) {
        if (dataBind != null && ((String) dataBind.get("name")).length() != 0) {
            name = dataBind.get("name");
        }
        return this.getObject(name, type);
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
