/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 懒加载
 * <p>
 * 标记了该注解的 Bean 不会在容器启动的时候加载实例化，而是当第一次使用的时候才会实例化
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:00
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Lazy {
}
