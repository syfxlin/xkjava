/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作用域
 * <p>
 * 标记 Bean 的作用域
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:48
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    @AliasFor("type")
    String value() default "singleton";

    @AliasFor("value")
    String type() default "singleton";
}
