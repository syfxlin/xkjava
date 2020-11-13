/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotations.WebBind.Type;
import me.ixk.framework.ioc.DataBinder.Converter;

/**
 * Session 值
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:50
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@WebBind(type = Type.SESSION)
public @interface SessionValue {
    @AliasFor(value = "name", annotation = WebBind.class, attribute = "value")
    String value() default "";

    @AliasFor(value = "value", annotation = WebBind.class, attribute = "name")
    String name() default "";

    @AliasFor(annotation = WebBind.class, attribute = "required")
    boolean required() default true;

    @AliasFor(annotation = WebBind.class, attribute = "converter")
    Class<? extends Converter>[] converter() default {  };
}
