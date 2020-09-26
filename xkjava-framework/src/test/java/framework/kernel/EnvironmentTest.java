/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package framework.kernel;

import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.test.XkJavaRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(XkJavaRunner.class)
class EnvironmentTest {

    @Test
    void all() {
        System.out.println(XkJava.of().make(Environment.class).all());
    }
}
