package me.ixk.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JSON {
    protected static Gson gson = new Gson();

    public static JsonObject parseObject(String json) {
        return gson.fromJson(json, JsonObject.class);
    }

    public static JsonArray parseArray(String json) {
        return gson.fromJson(json, JsonArray.class);
    }

    public static <T> T parse(String json, Class<T> _class) {
        return gson.fromJson(json, _class);
    }

    public static String stringify(JsonElement object) {
        return object.toString();
    }

    public static String stringify(Object object) {
        return gson.toJson(object);
    }
}
