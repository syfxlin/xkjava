/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.before.AspectRegistry;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Bean
@BeforeImport(AspectRegistry.class)
public @interface Aspect {
    @AliasFor("pointcut")
    String value() default "";

    @AliasFor("value")
    String pointcut() default "";
}
