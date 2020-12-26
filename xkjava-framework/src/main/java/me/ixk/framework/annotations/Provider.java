/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 提供者
 * <p>
 * 效果同 Configuration，更好的语义区分，主要用于提供核心 Bean，而非配置 Bean
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:06
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface Provider {
    @AliasFor(value = "name", annotation = Bean.class, attribute = "value")
    String[] value() default {};

    @AliasFor(value = "value", annotation = Bean.class, attribute = "name")
    String[] name() default {};

    @AliasFor(annotation = Bean.class, attribute = "type")
    Class<?>[] type() default {};
}
