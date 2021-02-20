/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.AliasFor;
import me.ixk.framework.http.HttpMethod;
import me.ixk.framework.http.MimeType;

/**
 * Put 响应
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 6:48
 */
@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@ClientResponse(method = HttpMethod.PUT)
public @interface PutResponse {
    @AliasFor(
        value = "url",
        annotation = ClientResponse.class,
        attribute = "value"
    )
    String value() default "";

    @AliasFor(
        value = "value",
        annotation = ClientResponse.class,
        attribute = "url"
    )
    String url() default "";

    @AliasFor(annotation = ClientResponse.class, attribute = "form")
    String[] form() default {  };

    @AliasFor(annotation = ClientResponse.class, attribute = "body")
    String body() default "";

    @AliasFor(annotation = ClientResponse.class, attribute = "contentType")
    MimeType contentType() default MimeType.NONE;

    @AliasFor(annotation = ClientResponse.class, attribute = "cookie")
    String[] cookie() default {  };

    @AliasFor(annotation = ClientResponse.class, attribute = "header")
    String[] header() default {  };
}
