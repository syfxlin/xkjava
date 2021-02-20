/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.cache.KeyGenerator;

/**
 * 删除缓存
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 2:16
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEvict {
    @AliasFor("cacheName")
    String value() default "";

    @AliasFor("value")
    String cacheName() default "";

    String key() default "";

    Class<? extends KeyGenerator> keyGenerator() default KeyGenerator.class;

    String condition() default "";

    boolean allEntries() default false;

    boolean beforeInvocation() default false;
}
