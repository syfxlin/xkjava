/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

/**
 * 在 Bean 初始化后执行，instance 绑定的 Bean 无法被处理
 */
public interface BeanBeforeProcessor {
    Object process(Container container, Binding binding, Object instance);
}
