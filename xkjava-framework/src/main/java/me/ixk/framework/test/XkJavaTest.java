/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.Component;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Otstar Lin
 * @date 2020/11/5 下午 4:30
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(XkJavaRunner.class)
@Component
public @interface XkJavaTest {
    String location() default "";

    String name() default "";

    String active() default "";

    String[] imports() default {};

    String[] args() default {};

    Class<?>[] classes() default {};
}
