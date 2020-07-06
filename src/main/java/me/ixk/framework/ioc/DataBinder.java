/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.convert.Convert;
import java.util.Map;

public class DataBinder {
    public static final String NO_SET = "NO_SET";
    public static final String DEFAULT_VALUE_PREFIX = "_";

    private final Container container;

    private String prefix = NO_SET;

    private Map<String, Object> bind;

    public DataBinder(Container container, Map<String, Object> bind) {
        this.container = container;
        this.bind = bind;
    }

    public <T> T getObject(String name, Class<T> type) {
        return this.getObject(name, type, "");
    }

    public <T> T getObject(String name, Class<T> type, String prefix) {
        Object object;
        String concatName = this.concat(prefix, name);
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
            this.prefix = this.concatPrefix(name);
            object = container.make(type.getName(), (Class<?>) type, this);
            this.prefix = oldPrefix;
        }
        return Convert.convert(type, object);
    }

    protected String concatPrefix(String name) {
        if (this.prefix.equals(NO_SET)) {
            return "";
        }
        return (this.prefix.length() == 0 ? "" : this.prefix + ".") + name;
    }

    protected String concat(String prefix, String name) {
        StringBuilder builder = new StringBuilder(
            this.prefix.length() == 0 || this.prefix.equals(NO_SET)
                ? ""
                : this.prefix + "."
        );
        if (prefix.length() != 0) {
            builder.append(prefix).append(".");
        }
        builder.append(name);
        return builder.toString();
    }

    public Map<String, Object> getBind() {
        return bind;
    }

    public void setBind(Map<String, Object> bind) {
        this.bind = bind;
    }
}
