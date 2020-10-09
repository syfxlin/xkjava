/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 效果同 Configuration，更好的语义区分，主要用于提供核心 Bean，而非配置 Bean
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface Provider {
    @AliasFor("name")
    String[] value() default {  };

    @AliasFor("value")
    String[] name() default {  };
}
