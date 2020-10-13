/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.MiddlewareRegistry;

/**
 * 中间件
 * <p>
 * 标记了该注解的路由会使用指定的中间件
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:01
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@AfterImport(MiddlewareRegistry.class)
public @interface Middleware {
    @AliasFor("name")
    String[] value() default {  };

    @AliasFor("value")
    String[] name() default {  };

    Class<? extends me.ixk.framework.middleware.Middleware>[] middleware() default me.ixk.framework.middleware.Middleware.class;
}
