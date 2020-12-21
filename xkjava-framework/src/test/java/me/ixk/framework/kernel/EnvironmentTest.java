/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.property.Environment;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

@XkJavaTest
class EnvironmentTest {

    @Test
    void all() {
        System.out.println(XkJava.of().make(Environment.class).all());
    }
}
