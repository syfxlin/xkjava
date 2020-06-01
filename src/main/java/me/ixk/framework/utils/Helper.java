package me.ixk.framework.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.security.SecureRandom;
import java.util.*;

public class Helper {
    protected static final String BASE_STRING =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    protected static final SecureRandom RANDOM = new SecureRandom();

    public static JsonNode dataGet(JsonNode target, String key) {
        return dataGet(target, key, null);
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
        return dataGet(target, keys, null);
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
                            null
                        );
                        if (element != null) {
                            array.add(element);
                        }
                    }
                } else if (target.isArray()) {
                    ArrayNode array1 = (ArrayNode) target;
                    for (JsonNode item : array1) {
                        JsonNode element = dataGet(item, subKeys, null);
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
        return dataGet(target, keys, _default);
    }

    public static Object dataGet(Object target, String[] keys) {
        return dataGet(target, keys, null);
    }

    @SuppressWarnings("unchecked")
    public static Object dataGet(
        Object target,
        String[] keys,
        Object _default
    ) {
        if (keys == null) {
            return target;
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
                            null
                        );
                        if (element != null) {
                            array.add(element);
                        }
                    }
                } else if (target instanceof List) {
                    List<Object> array1 = (List<Object>) target;
                    for (Object item : array1) {
                        Object element = dataGet(item, subKeys, null);
                        if (element != null) {
                            array.add(element);
                        }
                    }
                }
                return array;
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
        return target;
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
}
