package me.ixk.framework.kernel;

import cn.hutool.core.convert.Convert;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Helper;

public class Config {
    protected final Application app;

    public Config(Application app, Map<String, Map<String, Object>> config) {
        this.app = app;
        this.setConfigMap(config);
    }

    protected Map<String, Map<String, Object>> getConfigMap() {
        return this.app.getOrDefaultAttribute(
                "config",
                new ConcurrentHashMap<>()
            );
    }

    protected void setConfigMap(Map<String, Map<String, Object>> config) {
        this.app.setAttribute("config", config);
    }

    public Map<String, Map<String, Object>> all() {
        return this.getConfigMap();
    }

    public Object get(String name) {
        return this.get(name, null);
    }

    public Object get(String name, Object _default) {
        return Helper.dataGet(this.getConfigMap(), name, _default);
    }

    public <T> T get(String name, Object _default, Class<T> returnType) {
        return Convert.convert(returnType, this.get(name, _default));
    }

    protected void setItem(String name, Object value) {
        Helper.dataSet(this.getConfigMap(), name, value);
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
