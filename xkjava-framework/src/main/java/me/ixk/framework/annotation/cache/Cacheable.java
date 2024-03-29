/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.core.AliasFor;
import me.ixk.framework.cache.KeyGenerator;

/**
 * 可缓存
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 2:14
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    @AliasFor("cacheName")
    String value() default "";

    @AliasFor("value")
    String cacheName() default "";

    String key() default "";

    Class<? extends KeyGenerator> keyGenerator() default KeyGenerator.class;

    String condition() default "";

    String unless() default "";
}
