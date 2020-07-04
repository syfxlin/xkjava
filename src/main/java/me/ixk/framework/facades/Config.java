/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.facades;

import java.util.Map;

public class Config extends AbstractFacade {

    protected static me.ixk.framework.kernel.Config make() {
        return app.make(me.ixk.framework.kernel.Config.class);
    }

    public static Map<String, Map<String, Object>> all() {
        return make().all();
    }

    public static Object get(String name) {
        return make().get(name);
    }

    public static Object get(String name, Object _default) {
        return make().get(name, _default);
    }

    public static <T> T get(String name, Class<T> returnType) {
        return make().get(name, null, returnType);
    }

    public static <T> T get(String name, Object _default, Class<T> returnType) {
        return make().get(name, _default, returnType);
    }

    public static void set(String name, Object value) {
        make().set(name, value);
    }

    public static void set(Map<String, Object> values) {
        make().set(values);
    }

    public static void push(String name, Object value) {
        make().push(name, value);
    }

    public static boolean has(String name) {
        return make().has(name);
    }
}
