/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用（多）
 *
 * @author Otstar Lin
 * @date 2020/11/30 上午 11:27
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@RepeatItem(Enable.class)
public @interface Enables {
    Enable[] value();
}
