package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    @AliasFor("name")
    String[] value() default {  };

    @AliasFor("value")
    String[] name() default {  };
}
