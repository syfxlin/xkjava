/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.test.XkJavaTest;
import me.ixk.framework.test.event.AfterTestAll;
import me.ixk.framework.test.event.AfterTestEach;
import me.ixk.framework.test.event.AfterTestExecution;
import me.ixk.framework.test.event.BeforeTestAll;
import me.ixk.framework.test.event.BeforeTestEach;
import me.ixk.framework.test.event.BeforeTestExecution;
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
@Slf4j
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

    @BeforeTestAll
    void beforeAll() {
        log.info("BeforeTestAll");
    }

    @BeforeTestEach
    void beforeTestEach() {
        log.info("BeforeTestEach");
    }

    @BeforeTestExecution
    void beforeTestExecution() {
        log.info("BeforeTestExecution");
    }

    @AfterTestExecution
    void afterTestExecution() {
        log.info("AfterTestExecution");
    }

    @AfterTestEach
    void afterTestEach() {
        log.info("AfterTestEach");
    }

    @AfterTestAll
    void afterTestAll() {
        log.info("AfterTestAll");
    }
}
