/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.convert.Convert;
import java.util.Map;

public class DataBinder {
    public static final String DEFAULT_VALUE_PREFIX = "_";

    private final Container container;

    private boolean isFirst = true;

    private String prefix = "";

    private Map<String, Object> bind;

    public DataBinder(Container container, Map<String, Object> bind) {
        this.container = container;
        this.bind = bind;
    }

    public <T> T getObject(String name, Class<T> type) {
        return this.getObject(name, type, "");
    }

    public <T> T getObject(String name, Class<T> type, String prefix) {
        if (prefix != null) {
            this.prefix = prefix;
        }
        Object object;
        String concatName = this.concat(name);
        String typeName = type.getName();
        object = this.bind.get(concatName);
        if (object == null) {
            object = this.bind.get(DEFAULT_VALUE_PREFIX + concatName);
        }
        if (object == null) {
            object = this.bind.get(typeName);
        }
        if (object == null) {
            object = this.bind.get(DEFAULT_VALUE_PREFIX + typeName);
        }
        if (object == null) {
            String oldPrefix = this.prefix;
            boolean oldIsFirst = this.isFirst;
            this.prefix = this.concatPrefix(name);
            this.isFirst = false;
            object = container.make(type.getName(), (Class<?>) type, this);
            this.prefix = oldPrefix;
            this.isFirst = oldIsFirst;
        }
        return Convert.convert(type, object);
    }

    protected String concatPrefix(String name) {
        if (isFirst) {
            return this.prefix;
        }
        return (this.prefix.length() == 0 ? "" : this.prefix + ".") + name;
    }

    protected String concat(String name) {
        return (
            (isFirst || this.prefix.length() == 0 ? "" : this.prefix + ".") +
            name
        );
    }

    public Map<String, Object> getBind() {
        return bind;
    }

    public void setBind(Map<String, Object> bind) {
        this.bind = bind;
    }
}
