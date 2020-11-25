/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import static me.ixk.framework.helpers.Util.caseGet;

import cn.hutool.core.util.StrUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.Convert;

/**
 * 环境（配置）
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:23
 */
public class Environment {
    protected static final String PROPERTIES_SPLIT = ".";

    protected final XkJava app;
    protected volatile Properties properties;

    public Environment(XkJava app, Properties properties) {
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
        return this.get(key, (String) null);
    }

    public String get(String key, String defaultValue) {
        key = StrUtil.toCamelCase(key);
        final String value = caseGet(key, this.properties::getProperty);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public <T> T get(String key, Class<T> returnType) {
        return Convert.convert(returnType, get(key));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) Convert.convert(defaultValue.getClass(), value);
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

    public Integer getInt(String key, Integer defaultValue) {
        String value = this.get(key);
        if (value != null) {
            return Convert.toInt(value);
        }
        return defaultValue;
    }

    public Long getLong(String key) {
        return this.getLong(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        String value = this.get(key);
        if (value != null) {
            return Convert.toLong(value);
        }
        return defaultValue;
    }

    public Boolean getBoolean(String key) {
        return this.getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = this.get(key);
        if (value != null) {
            return Convert.toBool(value);
        }
        return defaultValue;
    }

    public Double getDouble(String key) {
        return this.getDouble(key, null);
    }

    public Double getDouble(String key, Double defaultValue) {
        String value = this.get(key);
        if (value != null) {
            return Convert.toDouble(value);
        }
        return defaultValue;
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
        if (!prefix.endsWith(PROPERTIES_SPLIT)) {
            prefix += PROPERTIES_SPLIT;
        }
        Map<String, Object> map = new ConcurrentHashMap<>(256);
        for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith(prefix)) {
                map.put(key.substring(prefix.length()), entry.getValue());
            }
        }
        return map;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new ConcurrentHashMap<>(256);
        for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
            map.put(entry.getKey().toString(), entry.getValue());
        }
        return map;
    }
}
