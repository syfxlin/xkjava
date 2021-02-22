/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.core.AliasFor;
import me.ixk.framework.annotation.core.Bean;

/**
 * Mybatis Plus Service
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:49
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface Service {
    @AliasFor(value = "name", annotation = Bean.class, attribute = "value")
    String[] value() default {};

    @AliasFor(value = "value", annotation = Bean.class, attribute = "name")
    String[] name() default {};

    @AliasFor(annotation = Bean.class, attribute = "type")
    Class<?>[] type() default {};
}
