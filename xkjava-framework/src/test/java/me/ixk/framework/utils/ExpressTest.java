/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.ixk.framework.test.XkJavaRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Otstar Lin
 * @date 2020/10/13 下午 8:58
 */
@ExtendWith(XkJavaRunner.class)
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
