package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandler {
    @AliasFor("exception")
    Class<? extends Throwable>[] value() default {  };

    @AliasFor("value")
    Class<? extends Throwable>[] exception() default {  };
}
