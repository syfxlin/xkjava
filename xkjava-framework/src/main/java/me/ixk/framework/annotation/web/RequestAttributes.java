/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.core.RepeatItem;

/**
 * 请求 Attribute 注入（多）
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:14
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@RepeatItem(RequestAttribute.class)
public @interface RequestAttributes {
    RequestAttribute[] value();
}
