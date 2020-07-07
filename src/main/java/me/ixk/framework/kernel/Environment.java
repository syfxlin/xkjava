/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Convert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Environment {
    protected final Application app;
    protected Properties properties;

    public Environment(Application app, Properties properties) {
        this.app = app;
        this.properties = properties;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public Environment setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public Properties all() {
        return this.properties;
    }

    public String get(String key) {
        return this.get(key, null);
    }

    public String get(String key, String _default) {
        return this.properties.getProperty(key, _default);
    }

    public Environment set(Map<String, String> values) {
        this.properties.putAll(values);
        return this;
    }

    public Environment set(String key, String value) {
        this.properties.setProperty(key, value);
        return this;
    }

    public boolean has(String key) {
        return this.properties.containsKey(key);
    }

    public Environment put(String key, String value) {
        this.set(key, value);
        return this;
    }

    public Environment putAll(Map<String, String> values) {
        this.set(values);
        return this;
    }

    public Environment merge(Environment environment) {
        this.properties.putAll(environment.getProperties());
        return this;
    }

    public Integer getInt(String key) {
        return this.getInt(key, null);
    }

    public Integer getInt(String key, Integer _default) {
        String value = this.get(key);
        if (value != null) {
            return Convert.toInt(value);
        }
        return _default;
    }

    public Long getLong(String key) {
        return this.getLong(key, null);
    }

    public Long getLong(String key, Long _default) {
        String value = this.get(key);
        if (value != null) {
            return Convert.toLong(value);
        }
        return _default;
    }

    public Boolean getBoolean(String key) {
        return this.getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean _default) {
        String value = this.get(key);
        if (value != null) {
            return Convert.toBool(value);
        }
        return _default;
    }

    public Double getDouble(String key) {
        return this.getDouble(key, null);
    }

    public Double getDouble(String key, Double _default) {
        String value = this.get(key);
        if (value != null) {
            return Convert.toDouble(value);
        }
        return _default;
    }

    public String[] getArray(String key, String split) {
        String values = this.get(key);
        if (values == null) {
            return null;
        }
        return values.split(split);
    }

    public List<String> getList(String key, String split) {
        String values = this.get(key);
        if (values == null) {
            return null;
        }
        return Arrays.asList(values.split(split));
    }

    public int size() {
        return this.properties.size();
    }

    public boolean isEmpty() {
        return this.properties.isEmpty();
    }

    public Map<String, Object> getPrefix(String prefix) {
        if (!prefix.endsWith(".")) {
            prefix += ".";
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith(prefix)) {
                map.put(key.substring(prefix.length()), entry.getValue());
            }
        }
        return map;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
            map.put(entry.getKey().toString(), entry.getValue());
        }
        return map;
    }
}
