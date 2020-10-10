/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotations.AliasFor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SuperParent
public @interface Parent {
    @AliasFor(
        annotation = SuperParent.class,
        attribute = "value",
        value = "name"
    )
    String value() default "";

    @AliasFor(
        annotation = SuperParent.class,
        attribute = "name",
        value = "value"
    )
    String name() default "";
}
