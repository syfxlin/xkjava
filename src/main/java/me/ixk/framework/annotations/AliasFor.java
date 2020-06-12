package me.ixk.framework.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AliasFor {
    String value() default "";

    Class<? extends Annotation> annotation() default Annotation.class;
}
