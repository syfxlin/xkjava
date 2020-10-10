/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registrar.MiddlewareRegistrar;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Attribute(name = "routeMiddleware", registrar = MiddlewareRegistrar.class)
public @interface RouteMiddleware {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";
}
