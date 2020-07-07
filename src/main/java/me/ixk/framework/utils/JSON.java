/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSON extends ObjectMapper {

    private JSON() {
        super();
    }

    public static JSON make() {
        return Inner.INSTANCE;
    }

    public static JSON getInstance() {
        return Inner.INSTANCE;
    }

    private static class Inner {
        private static final JSON INSTANCE = new JSON();
    }

    public static ObjectNode parseObject(String json) {
        try {
            return make().readValue(json, ObjectNode.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static ArrayNode parseArray(String json) {
        try {
            return make().readValue(json, ArrayNode.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static JsonNode parse(String json) {
        try {
            return make().readTree(json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> T parse(String json, Class<T> _class) {
        try {
            return make().readValue(json, _class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String stringify(Object object) {
        try {
            return make().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String stringify(JsonNode node) {
        return node.toString();
    }

    public static ObjectNode createObject() {
        return make().createObjectNode();
    }

    public static ArrayNode createArray() {
        return make().createArrayNode();
    }

    public static JsonNode convertToNode(Object object) {
        return make().valueToTree(object);
    }

    public static <T> T convertToObject(JsonNode node, Class<T> type) {
        try {
            return make().treeToValue(node, type);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
