/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/12/13 下午 2:53
 */
@XkJavaTest
class JwtTest {

    @Autowired
    Jwt jwt;

    @Test
    void jwt() throws InterruptedException {
        final String encode = jwt.encode(Map.of("key", "value"), 10L);
        final Map<String, String> decode = jwt.decode(encode);
        assertEquals("value", decode.get("key"));
        Thread.sleep(11 * 1000L);
        assertThrows(RuntimeException.class, () -> jwt.decode(encode));
    }
}
