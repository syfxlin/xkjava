/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotations.WebBind.Type;
import me.ixk.framework.http.Request;
import me.ixk.framework.ioc.DataBinder.Converter;

/**
 * Body 值
 * <p>
 * 注入请求体到参数，可以是 JSON，也可以 Form，若是 JSON 则会转化成 JsonNode 然后读取值。
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:26
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@WebBind(type = Type.BODY)
public @interface BodyValue {
    @AliasFor(value = "name", annotation = WebBind.class, attribute = "value")
    String value() default Request.REQUEST_BODY;

    @AliasFor(value = "value", annotation = WebBind.class, attribute = "name")
    String name() default Request.REQUEST_BODY;

    @AliasFor(annotation = WebBind.class, attribute = "required")
    boolean required() default true;

    @AliasFor(annotation = WebBind.class, attribute = "defaultValue")
    String defaultValue() default DataBind.EMPTY;

    @AliasFor(annotation = WebBind.class, attribute = "converter")
    Class<? extends Converter>[] converter() default {  };
}
