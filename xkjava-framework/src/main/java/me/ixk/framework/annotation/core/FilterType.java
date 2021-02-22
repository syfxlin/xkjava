/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.core;

/**
 * @author Otstar Lin
 * @date 2020/11/29 下午 2:38
 */
public enum FilterType {
    /**
     * 注解
     */
    ANNOTATION,
    /**
     * 类型
     */
    ASSIGNABLE_TYPE,
    /**
     * 正则表达式
     */
    REGEX,
    /**
     * 自定义
     */
    CUSTOM,
}
