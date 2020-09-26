/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Middleware {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    Class<? extends me.ixk.framework.middleware.Middleware>[] middleware() default me.ixk.framework.middleware.Middleware.class;
}