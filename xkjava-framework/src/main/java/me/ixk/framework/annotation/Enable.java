/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.attribute.EnableAttributeRegistry;

/**
 * 启用
 *
 * @author Otstar Lin
 * @date 2020/11/30 上午 11:09
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Attribute(name = "enableFunctions", registry = EnableAttributeRegistry.class)
@Repeatable(Enables.class)
public @interface Enable {
    @AliasFor("name")
    String[] value() default {};

    @AliasFor("value")
    String[] name() default {};

    Class<?>[] classes() default {};
}
