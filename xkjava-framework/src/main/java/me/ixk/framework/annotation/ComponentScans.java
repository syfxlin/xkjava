/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解扫描（多）
 * <p>
 * 介绍见 @ComponentScan
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:29
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@RepeatItem(ComponentScan.class)
public @interface ComponentScans {
    ComponentScan[] value();
}
