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
 * 缓存配置
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 2:15
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheConfig {
    String cacheName() default "";

    Class<? extends KeyGenerator> keyGenerator() default KeyGenerator.class;
}
