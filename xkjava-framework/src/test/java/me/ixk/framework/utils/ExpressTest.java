/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/10/13 下午 8:58
 */
@XkJavaTest
class ExpressTest {

    @Test
    void evaluateApp() {
        final String username = Express.evaluateApp(
            "#e['database.username']",
            String.class
        );
        assertEquals(username, "syfxlin");
    }
}
