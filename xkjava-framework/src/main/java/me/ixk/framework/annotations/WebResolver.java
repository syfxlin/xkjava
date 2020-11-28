/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.WebResolverRegistry;

/**
 * Web 解析器
 * <p>
 * 用于设置请求参数的解析器，响应解析器
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:57
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AfterRegistry(WebResolverRegistry.class)
public @interface WebResolver {
}
