package me.ixk.framework.utils;

import cn.hutool.core.convert.Convert;
import java.util.Map;
import me.ixk.framework.ioc.Application;

public class Config {
    protected Application app;

    public Config(Application app) {
        this.app = app;
    }

    public Map<String, Map<String, Object>> all() {
        return this.app.getConfig();
    }

    public Object get(String name) {
        return this.get(name, null);
    }

    public Object get(String name, Object _default) {
        return Helper.dataGet(this.app.getConfig(), name, _default);
    }

    public <T> T get(String name, Object _default, Class<T> returnType) {
        return Convert.convert(returnType, this.get(name, _default));
    }

    protected void setItem(String name, Object value) {
        // TODO: dataSet
    }

    public void set(String name, Object value) {
        this.setItem(name, value);
    }

    public void set(Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            this.setItem(entry.getKey(), entry.getValue());
        }
    }

    public void push(String name, Object value) {
        this.set(name, value);
    }

    public boolean has(String name) {
        return this.get(name) != null;
    }
}
