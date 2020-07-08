/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.test.XkJavaRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(XkJavaRunner.class)
class EnvironmentTest {

    @Test
    void all() {
        System.out.println(Application.get().make(Environment.class).all());
    }
}
