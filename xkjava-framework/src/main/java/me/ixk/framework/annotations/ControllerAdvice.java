/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registrar.ExceptionHandlerRegistrar;
import me.ixk.framework.registrar.InitBinderRegistrar;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Lazy
@Bean
@Attributes(
    {
        @Attribute(
            name = "adviceInitBinderHandlerResolver",
            registrar = InitBinderRegistrar.class
        ),
        @Attribute(
            name = "adviceExceptionHandlerResolvers",
            registrar = ExceptionHandlerRegistrar.class
        ),
    }
)
public @interface ControllerAdvice {
}
