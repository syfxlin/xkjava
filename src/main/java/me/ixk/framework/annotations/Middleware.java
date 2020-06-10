package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Middleware {
    String value() default "";

    Class<? extends me.ixk.framework.middleware.Middleware> middleware() default me.ixk.framework.middleware.Middleware.class;
}