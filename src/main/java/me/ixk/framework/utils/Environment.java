package me.ixk.framework.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Environment {
    protected static Properties property;

    public Environment(String path) {
        property = new Properties();
        try {
            property.load(this.getClass().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties all() {
        return property;
    }

    public String get(String key) {
        return this.get(key, null);
    }

    public String get(String key, String _default) {
        return property.getProperty(key, _default);
    }

    public void set(Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
    }

    public void set(String key, String value) {
        property.setProperty(key, value);
    }

    public boolean has(String key) {
        return property.containsKey(key);
    }

    public void push(String key, String value) {
        this.set(key, value);
    }
}
