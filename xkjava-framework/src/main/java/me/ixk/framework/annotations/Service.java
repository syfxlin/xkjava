/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.ioc.context.ScopeType;

/**
 * Mybatis Plus Service
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:49
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Scope(ScopeType.PROTOTYPE)
@Bean
public @interface Service {
    @AliasFor(value = "name", annotation = Bean.class, attribute = "value")
    String[] value() default {};

    @AliasFor(value = "value", annotation = Bean.class, attribute = "name")
    String[] name() default {};

    @AliasFor(annotation = Bean.class, attribute = "type")
    Class<?>[] type() default {};
}
