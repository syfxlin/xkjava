/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.test.XkJavaRunner;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Otstar Lin
 * @date 2020/11/5 下午 4:30
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(XkJavaRunner.class)
public @interface XkJavaTest {
    @AliasFor("properties")
    String[] value() default {  };

    @AliasFor("value")
    String[] properties() default {  };

    String[] args() default {  };

    Class<?>[] classes() default {  };
}