/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/29 下午 3:40
 */
@XkJavaTest
class BeanScannerTest {

    @Autowired
    XkJava app;

    @Test
    void test() {}
}
