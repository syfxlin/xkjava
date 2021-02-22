/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性（多）
 * <p>
 * 介绍见 @Attribute。
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:18
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@RepeatItem(Attribute.class)
public @interface Attributes {
    Attribute[] value();
}
