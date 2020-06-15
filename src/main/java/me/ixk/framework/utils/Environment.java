package me.ixk.framework.utils;

import java.util.Map;
import java.util.Properties;
import me.ixk.framework.ioc.Application;

public class Environment {
    protected final Application app;

    public Environment(Application app) {
        this.app = app;
    }

    public Properties all() {
        return this.app.getEnvironment();
    }

    public String get(String key) {
        return this.get(key, null);
    }

    public String get(String key, String _default) {
        return this.app.getEnvironment().getProperty(key, _default);
    }

    public void set(Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
    }

    public void set(String key, String value) {
        this.app.getEnvironment().setProperty(key, value);
    }

    public boolean has(String key) {
        return this.app.getEnvironment().containsKey(key);
    }

    public void push(String key, String value) {
        this.set(key, value);
    }
}
