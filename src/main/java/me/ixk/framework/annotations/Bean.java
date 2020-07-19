/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    @AliasFor("name")
    String[] value() default {  };

    @AliasFor("value")
    String[] name() default {  };

    String initMethod() default "";

    String destroyMethod() default "";

    BindType bindType() default BindType.NO_SET;

    enum BindType {
        NO_SET,
        BIND,
        NO_BIND,
    }
}
