/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.attribute.AttributeRegistry;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Attributes.class)
public @interface Attribute {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    Class<? extends AttributeRegistry> registry() default AttributeRegistry.class;

    boolean after() default false;
}
