package me.ixk.framework.kernel;

import java.util.Map;
import java.util.Properties;
import me.ixk.framework.ioc.Application;

public class Environment {
    protected final Application app;

    public Environment(Application app, Properties properties) {
        this.app = app;
        this.setProperties(properties);
    }

    protected Properties getProperties() {
        return this.app.getOrDefaultAttribute("env", new Properties());
    }

    protected void setProperties(Properties properties) {
        this.app.setAttribute("env", properties);
    }

    public Properties all() {
        return this.getProperties();
    }

    public String get(String key) {
        return this.get(key, null);
    }

    public String get(String key, String _default) {
        return this.getProperties().getProperty(key, _default);
    }

    public void set(Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
    }

    public void set(String key, String value) {
        this.getProperties().setProperty(key, value);
    }

    public boolean has(String key) {
        return this.getProperties().containsKey(key);
    }

    public void push(String key, String value) {
        this.set(key, value);
    }
}
