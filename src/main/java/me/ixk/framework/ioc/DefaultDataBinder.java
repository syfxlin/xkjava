/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.convert.Convert;
import java.util.Map;
import me.ixk.framework.annotations.DataBind;

public class DefaultDataBinder implements DataBinder {
    private final Container container;

    private Map<String, Object> bind;

    public DefaultDataBinder(Container container, Map<String, Object> bind) {
        this.container = container;
        this.bind = bind;
    }

    public <T> T getObject(String name, Class<T> type) {
        Object object = this.bind.get(name);
        if (object == null) {
            object = this.bind.get(type.getName());
        }
        if (object == null) {
            object = container.make(type, this);
        }
        return Convert.convert(type, object);
    }

    public <T> T getObject(String name, Class<T> type, DataBind dataBind) {
        if (dataBind != null) {
            name = dataBind.name();
        }
        return this.getObject(name, type);
    }

    public Map<String, Object> getBind() {
        return bind;
    }

    public void setBind(Map<String, Object> bind) {
        this.bind = bind;
    }
}
