/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import me.ixk.framework.utils.LinkedCaseInsensitiveMultiValueMap;
import me.ixk.framework.utils.MultiValueMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LinkedCaseInsensitiveMultiValueMapTest {

    @Test
    void multiValue() {
        MultiValueMap<String, String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("QWE", "1");
        map.add("QWE", "2");
        map.add("qwe", "3");
        Assertions.assertEquals(3, map.get("qWe").size());
        map.set("qwe", "1");
        Assertions.assertEquals(1, map.get("qwe").size());
        Assertions.assertEquals("1", map.getOne("qwe"));
    }
}
