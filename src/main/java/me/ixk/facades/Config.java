package me.ixk.facades;

import java.util.Map;

public class Config extends AbstractFacade {
    protected static Class<?> _class = me.ixk.utils.Config.class;

    public static Map<String, Map<String, Object>> all() {
        return invoke(_class, "all", Map.class);
    }

    public static Object get(String name) {
        return invoke(_class, "get", Object.class, name);
    }

    public static Object get(String name, Object _default) {
        return invoke(_class, "get", Object.class, name, _default);
    }

    public static void set(String name, Object value) {
        invoke(_class, "set", null, name, value);
    }

    public static void set(Map<String, Object> values) {
        invoke(_class, "set", null, values);
    }

    public static void push(String name, Object value) {
        invoke(_class, "push", null, name, value);
    }

    public static boolean has(String name) {
        return invoke(_class, "has", boolean.class, name);
    }
}
