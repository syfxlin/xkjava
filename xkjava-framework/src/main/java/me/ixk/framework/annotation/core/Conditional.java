/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.ioc.Condition;

/**
 * 条件
 * <p>
 * 用于控制 Bean 的绑定条件，只有当所有 Condition 都为 true 时才会执行被标记的方法，类
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:30
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {
    Class<? extends Condition>[] value();
}
