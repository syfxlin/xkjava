/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.ServletComponentRegistry;

/**
 * 监听器
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 8:12
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Bean
@AfterRegistry(ServletComponentRegistry.class)
public @interface Listener {
    @AliasFor(value = "name", annotation = Bean.class, attribute = "value")
    String[] value() default {};

    @AliasFor(value = "value", annotation = Bean.class, attribute = "name")
    String[] name() default {};

    @AliasFor(annotation = Bean.class, attribute = "type")
    Class<?>[] type() default {};
}
