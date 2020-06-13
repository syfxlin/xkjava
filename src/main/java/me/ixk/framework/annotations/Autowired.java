package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    Class<?> type() default Class.class;

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";
}
