/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.http.HttpMethod;
import me.ixk.framework.kernel.CorsProcessor;
import me.ixk.framework.middleware.Cors;
import me.ixk.framework.registry.request.CrossOriginRegistry;

/**
 * 跨域资源共享
 * <p>
 * 标记该注解的路由才允许跨域
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:47
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@RequestAttribute(
    name = "me.ixk.framework.annotation.CrossOrigin",
    registry = CrossOriginRegistry.class
)
@Middleware(middleware = Cors.class)
public @interface CrossOrigin {
    @AliasFor("origin")
    String value() default CorsProcessor.DYNAMIC_ORIGIN;

    @AliasFor("value")
    String origin() default CorsProcessor.DYNAMIC_ORIGIN;

    String[] allowedHeaders() default {  };

    HttpMethod[] methods() default {  };

    boolean allowCredentials() default false;
}
