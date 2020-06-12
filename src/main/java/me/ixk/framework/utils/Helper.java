package me.ixk.framework.utils;

import cn.hutool.core.convert.Convert;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;

public abstract class Helper {
    protected static final String BASE_STRING =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    protected static final SecureRandom RANDOM = new SecureRandom();

    public static JsonNode dataGet(JsonNode target, String key) {
        return dataGet(target, key, null, JsonNode.class);
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
        return dataGet(target, keys, null, JsonNode.class);
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
            if (keys[i].equals("*")) {
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
        if (target == null) {
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
            if (keys[i].equals("*")) {
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

    public static String routeHandler(String handler) {
        if (handler.indexOf('.') == -1) {
            handler = "me.ixk.app.controllers." + handler;
        }
        return handler;
    }

    public static String routeHandler(Class<?> _class, String method) {
        return _class.getName() + "@" + method;
    }

    public static String routeHandler(Method method) {
        return method.getDeclaringClass().getName() + "@" + method.getName();
    }

    public static String routeHandler(String[] handler) {
        return handler[0] + "@" + handler[1];
    }
}
