/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * XkJavaTest 注解测试
 *
 * @author Otstar Lin
 * @date 2020/11/9 上午 11:24
 */
@XkJavaTest(
    location = "classpath:/",
    imports = { "classpath:/test.properties" },
    args = "--test-args=true",
    classes = XkJava.class
)
class XKJavaTestTest {

    @Autowired
    public XkJava app;

    @Test
    void properties() {
        assertTrue(app.env().getBoolean("test"));
    }

    @Test
    void args() {
        assertTrue(app.env().getBoolean("test-args"));
    }

    @Test
    void classes() {
        assertArrayEquals(
            new Class[] { XkJava.class, XKJavaTestTest.class },
            app.primarySource()
        );
    }
}
