package me.ixk.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;

public class Helper {

    public static JsonElement dataGet(JsonElement target, String key) {
        return dataGet(target, key, null);
    }

    public static JsonElement dataGet(
        JsonElement target,
        String key,
        JsonElement _default
    ) {
        String[] keys = key.split("\\.");
        return dataGet(target, keys, _default);
    }

    public static JsonElement dataGet(JsonElement target, String[] keys) {
        return dataGet(target, keys, null);
    }

    public static JsonElement dataGet(
        JsonElement target,
        String[] keys,
        JsonElement _default
    ) {
        if (keys == null) {
            return target;
        }
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals("*")) {
                JsonArray array = new JsonArray();
                String[] subKeys = Arrays.copyOfRange(keys, i + 1, keys.length);
                if (target.isJsonObject()) {
                    JsonObject object = target.getAsJsonObject();
                    for (String itemKey : object.keySet()) {
                        JsonElement element = dataGet(
                            object.get(itemKey),
                            subKeys,
                            null
                        );
                        if (element != null) {
                            array.add(element);
                        }
                    }
                } else if (target.isJsonArray()) {
                    JsonArray array1 = target.getAsJsonArray();
                    for (JsonElement item : array1) {
                        JsonElement element = dataGet(item, subKeys, null);
                        if (element != null) {
                            array.add(element);
                        }
                    }
                }
                return array;
            } else if (target.isJsonArray()) {
                JsonArray array = target.getAsJsonArray();
                int index = Integer.parseInt(keys[i]);
                if (array.size() <= index) {
                    target = null;
                    break;
                }
                target = array.get(index);
            } else if (target.isJsonObject()) {
                JsonObject object = target.getAsJsonObject();
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
}
