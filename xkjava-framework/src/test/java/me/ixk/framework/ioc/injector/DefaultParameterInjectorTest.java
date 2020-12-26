/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/12/26 下午 10:06
 */
@XkJavaTest
class DefaultParameterInjectorTest {

    @Autowired
    private MethodValueProperties methodValueProperties;

    @Test
    void methodValue() {
        assertNotNull(methodValueProperties.getValue());
    }
}
