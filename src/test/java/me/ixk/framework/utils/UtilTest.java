/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import me.ixk.framework.helpers.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class UtilTest {

    @Test
    void testJsonNodeDataSet() {
        ObjectNode objectNode = JSON.createObject();
        objectNode.set("sub", JSON.createArray());
        Util.dataSet(objectNode, "key", TextNode.valueOf("value"));
        Util.dataSet(objectNode, "sub.0", TextNode.valueOf("array0"));
        Util.dataSet(objectNode, "sub.2", TextNode.valueOf("array2"));
        Util.dataSet(objectNode, "sub1.sub2", TextNode.valueOf("sub3"));
        Util.dataSet(objectNode, "sub.3.data", TextNode.valueOf("data"));
        Assertions.assertEquals(
            "{\"sub\":[\"array0\",null,\"array2\",{\"data\":\"data\"}],\"key\":\"value\",\"sub1\":{\"sub2\":\"sub3\"}}",
            JSON.stringify(objectNode)
        );
    }

    @Test
    void testJsonNodeDataGet() {
        ObjectNode objectNode = JSON.parseObject(
            "{\"sub\":[\"array0\",null,\"array2\",{\"data\":\"data\"}],\"key\":\"value\",\"sub1\":{\"sub2\":\"sub3\"}}"
        );
        Assertions.assertEquals(
            "value",
            Util.dataGet(objectNode, "key").asText()
        );
        Assertions.assertEquals(
            "array0",
            Util.dataGet(objectNode, "sub.0").asText()
        );
        Assertions.assertEquals(
            "array2",
            Util.dataGet(objectNode, "sub.2").asText()
        );
        Assertions.assertEquals(
            "sub3",
            Util.dataGet(objectNode, "sub1.sub2").asText()
        );
        Assertions.assertEquals(
            "data",
            Util.dataGet(objectNode, "sub.3.data").asText()
        );
        Assertions.assertEquals(
            "value",
            Util
                .dataGet(objectNode, "sub.10", TextNode.valueOf("value"))
                .asText()
        );
    }

    @Test
    void testObjectDataSet() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("sub", new ArrayList<>());
        Util.dataSet(map, "key", "value");
        Util.dataSet(map, "sub.0", "array0");
        Util.dataSet(map, "sub.2", "array2");
        Util.dataSet(map, "sub1.sub2", "sub3");
        Util.dataSet(map, "sub.3.data", "data");
        Assertions.assertEquals(
            "{\"sub\":[\"array0\",null,\"array2\",{\"data\":\"data\"}],\"sub1\":{\"sub2\":\"sub3\"},\"key\":\"value\"}",
            JSON.stringify(map)
        );
    }

    @Test
    void testObjectDataGet() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("sub", new ArrayList<>());
        Util.dataSet(map, "key", "value");
        Util.dataSet(map, "sub.0", "array0");
        Util.dataSet(map, "sub.2", "array2");
        Util.dataSet(map, "sub1.sub2", "sub3");
        Util.dataSet(map, "sub.3.data", "data");

        Assertions.assertEquals("value", Util.dataGet(map, "key"));
        Assertions.assertEquals("array0", Util.dataGet(map, "sub.0"));
        Assertions.assertEquals("array2", Util.dataGet(map, "sub.2"));
        Assertions.assertEquals("sub3", Util.dataGet(map, "sub1.sub2"));
        Assertions.assertEquals("data", Util.dataGet(map, "sub.3.data"));
        Assertions.assertEquals(
            "value",
            Util.dataGet(map, "sub.10", "value")
        );
    }
}
