/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/5 下午 4:17
 */
@XkJavaTest
class ContainerTest {
    @Autowired
    XkJava app;

    @Test
    void testAutowire() {
        assertNotNull(this.app);
    }
}
