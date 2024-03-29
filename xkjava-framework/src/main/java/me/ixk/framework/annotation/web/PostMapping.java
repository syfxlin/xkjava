/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.core.AliasFor;
import me.ixk.framework.http.HttpMethod;

/**
 * POST 路由
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:05
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = HttpMethod.POST)
public @interface PostMapping {
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
