package me.ixk.framework.utils;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.exceptions.LoadConfigException;
import me.ixk.framework.ioc.Application;

public class Config {
    protected static Map<String, Map<String, Object>> config;

    @SuppressWarnings("unchecked")
    public Config(Application app) {
        config = new ConcurrentHashMap<>();
        Set<Class<?>> classes = ClassUtil.getPackageClass("me.ixk.app.config");
        for (Class<?> _class : classes) {
            if (
                Modifier.isInterface(_class.getModifiers()) ||
                Modifier.isAbstract(_class.getModifiers())
            ) {
                continue;
            }
            try {
                Object instance = _class
                    .getConstructor(Application.class)
                    .newInstance(app);
                config.put(
                    (String) _class.getMethod("configName").invoke(instance),
                    (Map<String, Object>) _class
                        .getMethod("config")
                        .invoke(instance)
                );
            } catch (Exception e) {
                throw new LoadConfigException(
                    "Load [" + _class.getSimpleName() + "] config failed",
                    e
                );
            }
        }
    }

    public Map<String, Map<String, Object>> all() {
        return config;
    }

    public Object get(String name) {
        return this.get(name, null);
    }

    public Object get(String name, Object _default) {
        return Helper.dataGet(config, name, _default);
    }

    public <T> T get(String name, Object _default, Class<T> returnType) {
        return returnType.cast(this.get(name, _default));
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
