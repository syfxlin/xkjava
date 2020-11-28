/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.http.HttpMethod;
import me.ixk.framework.registry.after.RouteRegistry;

/**
 * 路由
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:15
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@AfterRegistry(RouteRegistry.class)
public @interface RequestMapping {
    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};

    HttpMethod[] method() default HttpMethod.GET;
}
