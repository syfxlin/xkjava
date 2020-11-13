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
 * HTTP 头字段值
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:54
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@WebBind(type = Type.HEADER)
public @interface HeaderValue {
    @AliasFor(value = "name", annotation = WebBind.class, attribute = "value")
    String value() default "";

    @AliasFor(value = "value", annotation = WebBind.class, attribute = "name")
    String name() default "";

    @AliasFor(annotation = WebBind.class, attribute = "required")
    boolean required() default true;

    @AliasFor(annotation = WebBind.class, attribute = "defaultValue")
    String defaultValue() default DataBind.EMPTY;

    Class<? extends Converter>[] converter() default {  };
}
