/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    int MEDIUM_PRECEDENCE = 0;

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    @AliasFor("order")
    int value() default MEDIUM_PRECEDENCE;

    @AliasFor("value")
    int order() default MEDIUM_PRECEDENCE;
}
