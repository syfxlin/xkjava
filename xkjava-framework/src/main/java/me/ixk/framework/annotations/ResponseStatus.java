/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.registry.request.ResponseStatusRegistry;

/**
 * 响应码
 * <p>
 * 声明式设置响应码
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:46
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@RequestAttribute(
    name = "me.ixk.framework.annotations.ResponseStatus",
    registry = ResponseStatusRegistry.class
)
@Middleware(middleware = me.ixk.framework.middleware.ResponseStatus.class)
public @interface ResponseStatus {
    @AliasFor("code")
    HttpStatus value() default HttpStatus.INTERNAL_SERVER_ERROR;

    @AliasFor("value")
    HttpStatus code() default HttpStatus.INTERNAL_SERVER_ERROR;

    String reason() default "";
}
