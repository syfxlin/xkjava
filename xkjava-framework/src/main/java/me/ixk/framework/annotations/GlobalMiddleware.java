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
 * 全局中间件
 * <p>
 * 标记全局中间件，全局中间件会在每个请求中都执行
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:53
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AfterRegistry(MiddlewareRegistry.class)
public @interface GlobalMiddleware {
}
