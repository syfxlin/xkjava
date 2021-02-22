/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证 CSRF Token
 * <p>
 * 当是写入操作的 HTTP 请求标记该注解就会验证 CSRF Token
 * <p>
 * 除了标记写入操作的 HTTP 请求，若要自动设置 CSRF Token，非写入请求也需要标记该注解，用于获取和生成 CSRF Token
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:53
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Middleware(middleware = me.ixk.framework.middleware.VerifyCsrfToken.class)
public @interface VerifyCsrf {
}
