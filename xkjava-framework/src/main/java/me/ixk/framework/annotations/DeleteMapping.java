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
 * DELETE 路由
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:49
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = HttpMethod.DELETE)
public @interface DeleteMapping {
    @AliasFor(
        value = "path",
        annotation = RequestMapping.class,
        attribute = "value"
    )
    String[] value() default {  };

    @AliasFor(
        value = "value",
        annotation = RequestMapping.class,
        attribute = "path"
    )
    String[] path() default {  };
}
