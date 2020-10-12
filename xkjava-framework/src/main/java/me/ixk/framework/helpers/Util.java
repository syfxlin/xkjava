/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.helpers;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import me.ixk.framework.utils.ClassUtils;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.JSON;

public abstract class Util {
    protected static final String BASE_STRING =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    protected static final SecureRandom RANDOM = new SecureRandom();

    public static JsonNode dataGet(JsonNode target, String key) {
        return dataGet(target, key, (JsonNode) null);
    }

    public static JsonNode dataGet(
        JsonNode target,
        String key,
        JsonNode _default
    ) {
        String[] keys = key.split("\\.");
        return dataGet(target, keys, _default);
    }

    public static JsonNode dataGet(JsonNode target, String[] keys) {
        return dataGet(target, keys, (JsonNode) null);
    }

    public static JsonNode dataGet(
        JsonNode target,
        String[] keys,
        JsonNode _default
    ) {
        if (keys == null) {
            return target;
        }
        for (int i = 0; i < keys.length; i++) {
            if ("*".equals(keys[i])) {
                ArrayNode array = JSON.createArray();
                String[] subKeys = Arrays.copyOfRange(keys, i + 1, keys.length);
                if (target.isObject()) {
                    ObjectNode object = (ObjectNode) target;
                    for (
                        Iterator<String> it = object.fieldNames();
                        it.hasNext();
                    ) {
                        String itemKey = it.next();
                        JsonNode element = dataGet(
                            object.get(itemKey),
                            subKeys,
                            null,
                            JsonNode.class
                        );
                        if (element != null) {
                            array.add(element);
                        }
                    }
                } else if (target.isArray()) {
                    ArrayNode array1 = (ArrayNode) target;
                    for (JsonNode item : array1) {
                        JsonNode element = dataGet(
                            item,
                            subKeys,
                            null,
                            JsonNode.class
                        );
                        if (element != null) {
                            array.add(element);
                        }
                    }
                }
                return array;
            } else if (target.isArray()) {
                ArrayNode array = (ArrayNode) target;
                int index = Integer.parseInt(keys[i]);
                if (array.size() <= index) {
                    target = null;
                    break;
                }
                target = array.get(index);
            } else if (target.isObject()) {
                ObjectNode object = (ObjectNode) target;
                if (!object.has(keys[i])) {
                    target = null;
                    break;
                }
                target = object.get(keys[i]);
            } else {
                target = null;
                break;
            }
        }
        if (target == null || target.isNull()) {
            return _default;
        }
        return target;
    }

    public static Object dataGet(Object target, String key) {
        return dataGet(target, key, null);
    }

    public static Object dataGet(Object target, String key, Object _default) {
        String[] keys = key.split("\\.");
        return dataGet(target, keys, _default, Object.class);
    }

    public static Object dataGet(Object target, String[] keys) {
        return dataGet(target, keys, null, Object.class);
    }

    public static <T> T dataGet(
        Object target,
        String key,
        Class<T> returnType
    ) {
        return dataGet(target, key, null, returnType);
    }

    public static <T> T dataGet(
        Object target,
        String key,
        T _default,
        Class<T> returnType
    ) {
        String[] keys = key.split("\\.");
        return dataGet(target, keys, _default, returnType);
    }

    public static <T> T dataGet(
        Object target,
        String[] keys,
        Class<T> returnType
    ) {
        return dataGet(target, keys, null, returnType);
    }

    @SuppressWarnings("unchecked")
    public static <T> T dataGet(
        Object target,
        String[] keys,
        T _default,
        Class<T> returnType
    ) {
        if (keys == null) {
            return Convert.convert(returnType, target);
        }
        for (int i = 0; i < keys.length; i++) {
            if ("*".equals(keys[i])) {
                List<Object> array = new ArrayList<>();
                String[] subKeys = Arrays.copyOfRange(keys, i + 1, keys.length);
                if (target instanceof Map) {
                    Map<String, Object> object = (Map<String, Object>) target;
                    for (String itemKey : object.keySet()) {
                        Object element = dataGet(
                            object.get(itemKey),
                            subKeys,
                            null,
                            returnType
                        );
                        if (element != null) {
                            array.add(element);
                        }
                    }
                } else if (target instanceof List) {
                    List<Object> array1 = (List<Object>) target;
                    for (Object item : array1) {
                        Object element = dataGet(
                            item,
                            subKeys,
                            null,
                            returnType
                        );
                        if (element != null) {
                            array.add(element);
                        }
                    }
                }
                return Convert.convert(returnType, array);
            } else if (target instanceof List) {
                List<Object> array = (List<Object>) target;
                int index = Integer.parseInt(keys[i]);
                if (array.size() <= index) {
                    target = null;
                    break;
                }
                target = array.get(index);
            } else if (target instanceof Map) {
                Map<String, Object> object = (Map<String, Object>) target;
                if (!object.containsKey(keys[i])) {
                    target = null;
                    break;
                }
                target = object.get(keys[i]);
            } else {
                target = null;
                break;
            }
        }
        if (target == null) {
            return _default;
        }
        return Convert.convert(returnType, target);
    }

    public static void dataSet(JsonNode target, String key, JsonNode value) {
        String[] keys = key.split("\\.");
        dataSet(target, keys, value);
    }

    public static void dataSet(JsonNode target, String[] keys, JsonNode value) {
        if (target == null) {
            return;
        }
        for (int i = 0; i < keys.length; i++) {
            if (i != keys.length - 1) {
                if (target.isObject()) {
                    ObjectNode node = (ObjectNode) target;
                    target = node.get(keys[i]);
                    if (target == null) {
                        target = JSON.createObject();
                        node.set(keys[i], target);
                    }
                } else if (target.isArray()) {
                    ArrayNode node = (ArrayNode) target;
                    int index = Integer.parseInt(keys[i]);
                    target = node.get(index);
                    if (target == null) {
                        target = JSON.createObject();
                        while (node.size() < index) {
                            node.addNull();
                        }
                        node.insert(index, target);
                    }
                } else {
                    throw new ClassCastException(
                        "Can not set value to ValueNode"
                    );
                }
            } else {
                if (target.isObject()) {
                    ObjectNode object = (ObjectNode) target;
                    object.set(keys[i], value);
                } else if (target.isArray()) {
                    ArrayNode array = (ArrayNode) target;
                    int index = Integer.parseInt(keys[i]);
                    while (array.size() < index) {
                        array.addNull();
                    }
                    array.insert(index, value);
                } else {
                    throw new ClassCastException(
                        "Can not set value to ValueNode"
                    );
                }
            }
        }
    }

    public static void dataSet(Object target, String key, Object value) {
        String[] keys = key.split("\\.");
        dataSet(target, keys, value);
    }

    @SuppressWarnings("unchecked")
    public static void dataSet(Object target, String[] keys, Object value) {
        if (target == null) {
            return;
        }
        for (int i = 0; i < keys.length; i++) {
            if (i != keys.length - 1) {
                if (target instanceof List) {
                    List<Object> list = (List<Object>) target;
                    int index = Integer.parseInt(keys[i]);
                    if (list.size() > index) {
                        target = list.get(index);
                    } else {
                        target = new ConcurrentHashMap<String, Object>();
                        while (list.size() < index) {
                            list.add(null);
                        }
                        list.add(index, target);
                    }
                } else if (target instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) target;
                    target = map.get(keys[i]);
                    if (target == null) {
                        target = new ConcurrentHashMap<>();
                        map.put(keys[i], target);
                    }
                } else {
                    throw new ClassCastException(
                        "Can not set value to " + target.getClass().getName()
                    );
                }
            } else {
                if (target instanceof List) {
                    List<Object> list = (List<Object>) target;
                    int index = Integer.parseInt(keys[i]);
                    while (list.size() < index) {
                        list.add(null);
                    }
                    list.add(index, value);
                } else if (target instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) target;
                    map.put(keys[i], value);
                } else {
                    throw new ClassCastException(
                        "Can not set value to " + target.getClass().getName()
                    );
                }
            }
        }
    }

    public static String strRandom() {
        return strRandom(10);
    }

    public static String strRandom(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(
                BASE_STRING.charAt(RANDOM.nextInt(BASE_STRING.length()))
            );
        }
        return stringBuilder.toString();
    }

    public static String routeHandler(Class<?> clazz, String method) {
        return clazz.getName() + "@" + method;
    }

    public static String routeHandler(Method method) {
        return method.getDeclaringClass().getName() + "@" + method.getName();
    }

    public static String routeHandler(String[] handler) {
        return handler[0] + "@" + handler[1];
    }

    public static HandlerDefinition routeHandler(String handler) {
        final String[] handlerArr = handler.split("@");
        final Class<?> clazz = ClassUtil.loadClass(handlerArr[0]);
        final Method method = (Method) ClassUtils.getUserMethod(
            ReflectUtil.getMethodByName(clazz, handlerArr[1])
        );
        return new HandlerDefinition(clazz, method);
    }

    public static <T> T caseGet(String name, Function<String, T> fun) {
        return caseGet(
            name,
            fun,
            new char[] { '_', '-', 'a' },
            Objects::nonNull
        );
    }

    public static <T> T caseGet(
        String name,
        Function<String, T> fun,
        Predicate<T> predicate
    ) {
        return caseGet(name, fun, new char[] { '_', '-', 'a' }, predicate);
    }

    public static <T> T caseGet(
        String name,
        Function<String, T> fun,
        char[] splits,
        Predicate<T> predicate
    ) {
        T target = fun.apply(name);
        if (!predicate.test(target)) {
            for (char split : splits) {
                target =
                    fun.apply(
                        split == 'a'
                            ? StrUtil.toCamelCase(name)
                            : StrUtil.toSymbolCase(name, split)
                    );
                if (target == NullNode.getInstance()) {
                    target = null;
                }
                if (predicate.test(target)) {
                    break;
                }
            }
        }
        return target;
    }

    public static String attributeName(Class<?> clazz, String name) {
        return clazz.getName() + "." + name;
    }

    public static class HandlerDefinition {
        private final Class<?> controllerClass;
        private final Method method;

        public HandlerDefinition(Class<?> controllerClass, Method method) {
            this.controllerClass = controllerClass;
            this.method = method;
        }

        public Class<?> getControllerClass() {
            return controllerClass;
        }

        public Method getMethod() {
            return method;
        }
    }
}
