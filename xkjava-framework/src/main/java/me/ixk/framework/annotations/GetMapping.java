/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.http.HttpMethod;

/**
 * GET 路由
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:53
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = HttpMethod.GET)
public @interface GetMapping {
    @AliasFor(
        value = "path",
        annotation = RequestMapping.class,
        attribute = "value"
    )
    String[] value() default {};

    @AliasFor(
        value = "value",
        annotation = RequestMapping.class,
        attribute = "path"
    )
    String[] path() default {};
}
