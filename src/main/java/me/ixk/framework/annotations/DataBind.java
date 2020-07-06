/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(
    { ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.FIELD }
)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataBind {
    @AliasFor("prefix")
    String value() default "";

    @AliasFor("value")
    String prefix() default "";
}
