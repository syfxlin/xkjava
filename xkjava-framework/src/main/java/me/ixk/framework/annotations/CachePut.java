/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.cache.KeyGenerator;

/**
 * 设置缓存
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 2:17
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Cacheable
public @interface CachePut {
    @AliasFor(
        value = "cacheName",
        annotation = Cacheable.class,
        attribute = "value"
    )
    String value() default "";

    @AliasFor(
        value = "value",
        annotation = Cacheable.class,
        attribute = "cacheName"
    )
    String cacheName() default "";

    @AliasFor(annotation = Cacheable.class, attribute = "key")
    String key() default "";

    @AliasFor(annotation = Cacheable.class, attribute = "keyGenerator")
    Class<? extends KeyGenerator> keyGenerator() default KeyGenerator.class;

    @AliasFor(annotation = Cacheable.class, attribute = "condition")
    String condition() default "";

    @AliasFor(annotation = Cacheable.class, attribute = "unless")
    String unless() default "";
}
