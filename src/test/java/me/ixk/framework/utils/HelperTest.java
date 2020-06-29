package me.ixk.framework.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HelperTest {

    @Test
    void testJsonNodeDataSet() {
        ObjectNode objectNode = JSON.createObject();
        objectNode.set("sub", JSON.createArray());
        Helper.dataSet(objectNode, "key", TextNode.valueOf("value"));
        Helper.dataSet(objectNode, "sub.0", TextNode.valueOf("array0"));
        Helper.dataSet(objectNode, "sub.2", TextNode.valueOf("array2"));
        Helper.dataSet(objectNode, "sub1.sub2", TextNode.valueOf("sub3"));
        Helper.dataSet(objectNode, "sub.3.data", TextNode.valueOf("data"));
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
            Helper.dataGet(objectNode, "key").asText()
        );
        Assertions.assertEquals(
            "array0",
            Helper.dataGet(objectNode, "sub.0").asText()
        );
        Assertions.assertEquals(
            "array2",
            Helper.dataGet(objectNode, "sub.2").asText()
        );
        Assertions.assertEquals(
            "sub3",
            Helper.dataGet(objectNode, "sub1.sub2").asText()
        );
        Assertions.assertEquals(
            "data",
            Helper.dataGet(objectNode, "sub.3.data").asText()
        );
        Assertions.assertEquals(
            "value",
            Helper
                .dataGet(objectNode, "sub.10", TextNode.valueOf("value"))
                .asText()
        );
    }

    @Test
    void testObjectDataSet() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("sub", new ArrayList<>());
        Helper.dataSet(map, "key", "value");
        Helper.dataSet(map, "sub.0", "array0");
        Helper.dataSet(map, "sub.2", "array2");
        Helper.dataSet(map, "sub1.sub2", "sub3");
        Helper.dataSet(map, "sub.3.data", "data");
        Assertions.assertEquals(
            "{\"sub\":[\"array0\",null,\"array2\",{\"data\":\"data\"}],\"sub1\":{\"sub2\":\"sub3\"},\"key\":\"value\"}",
            JSON.stringify(map)
        );
    }

    @Test
    void testObjectDataGet() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("sub", new ArrayList<>());
        Helper.dataSet(map, "key", "value");
        Helper.dataSet(map, "sub.0", "array0");
        Helper.dataSet(map, "sub.2", "array2");
        Helper.dataSet(map, "sub1.sub2", "sub3");
        Helper.dataSet(map, "sub.3.data", "data");

        Assertions.assertEquals("value", Helper.dataGet(map, "key"));
        Assertions.assertEquals("array0", Helper.dataGet(map, "sub.0"));
        Assertions.assertEquals("array2", Helper.dataGet(map, "sub.2"));
        Assertions.assertEquals("sub3", Helper.dataGet(map, "sub1.sub2"));
        Assertions.assertEquals("data", Helper.dataGet(map, "sub.3.data"));
        Assertions.assertEquals(
            "value",
            Helper.dataGet(map, "sub.10", "value")
        );
    }
}
