/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import cn.hutool.core.convert.Convert;
import me.ixk.framework.helpers.Util;
import me.ixk.framework.ioc.Application;

import java.util.Map;

public class Config {
    protected final Application app;
    protected Map<String, Map<String, Object>> config;

    public Config(Application app, Map<String, Map<String, Object>> config) {
        this.app = app;
        this.config = config;
    }

    public Map<String, Map<String, Object>> getConfig() {
        return this.config;
    }

    public Config setConfig(Map<String, Map<String, Object>> config) {
        this.config = config;
        return this;
    }

    public Map<String, Map<String, Object>> all() {
        return this.config;
    }

    public Object get(String name) {
        return this.get(name, null);
    }

    public Object get(String name, Object _default) {
        return Util.dataGet(this.config, name, _default);
    }

    public <T> T get(String name, Class<T> returnType) {
        return this.get(name, null, returnType);
    }

    public <T> T get(String name, Object _default, Class<T> returnType) {
        return Convert.convert(returnType, this.get(name, _default));
    }

    protected Config setItem(String name, Object value) {
        Util.dataSet(this.config, name, value);
        return this;
    }

    public Config set(String name, Object value) {
        return this.setItem(name, value);
    }

    public Config set(Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            this.setItem(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Config put(String name, Object value) {
        this.set(name, value);
        return this;
    }

    public boolean has(String name) {
        return this.get(name) != null;
    }

    public Config putAll(Map<String, Object> values) {
        this.set(values);
        return this;
    }

    public Config merge(Config config) {
        this.config.putAll(config.getConfig());
        return this;
    }

    public Integer getInt(String key) {
        return this.getInt(key, null);
    }

    public Integer getInt(String key, Integer _default) {
        Object value = this.get(key);
        if (value != null) {
            return Convert.toInt(value);
        }
        return _default;
    }

    public Long getLong(String key) {
        return this.getLong(key, null);
    }

    public Long getLong(String key, Long _default) {
        Object value = this.get(key);
        if (value != null) {
            return Convert.toLong(value);
        }
        return _default;
    }

    public Boolean getBoolean(String key) {
        return this.getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean _default) {
        Object value = this.get(key);
        if (value != null) {
            return Convert.toBool(value);
        }
        return _default;
    }

    public Double getDouble(String key) {
        return this.getDouble(key, null);
    }

    public Double getDouble(String key, Double _default) {
        Object value = this.get(key);
        if (value != null) {
            return Convert.toDouble(value);
        }
        return _default;
    }

    public int size() {
        return this.config.size();
    }

    public boolean isEmpty() {
        return this.config.isEmpty();
    }
}
