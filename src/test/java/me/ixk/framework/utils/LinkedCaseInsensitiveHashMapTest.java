/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class LinkedCaseInsensitiveHashMapTest {

    @Test
    void caseInsensitive() {
        Map<String, String> map = new LinkedCaseInsensitiveHashMap<>();
        map.put("ABC", "1");
        Map<String, String> putMap = new HashMap<>();
        putMap.put("QWE", "2");
        putMap.put("ASD", "3");
        map.putAll(putMap);
        Assertions.assertEquals("1", map.get("abc"));
        Assertions.assertEquals("1", map.get("abC"));
        Assertions.assertTrue(map.containsKey("abc"));
        Assertions.assertEquals("2", map.get("Qwe"));
        Assertions.assertEquals("3", map.get("ASD"));
        Assertions.assertEquals("4", map.getOrDefault("a", "4"));
    }
}
