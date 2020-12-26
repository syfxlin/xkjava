/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.entity.ConstructorContext;
import me.ixk.framework.ioc.entity.InjectContext;

/**
 * Bean 创建后置处理器
 * <p>
 * 在 Bean 初始化前执行，instance 绑定的 Bean 无法被处理
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:08
 */
public interface BeanAfterCreateProcessor {
    /**
     * 处理
     *
     * @param container   容器
     * @param instance    Bean 实例
     * @param context     实例上下文
     * @param constructor 构造参数
     *
     * @return Bean 实例
     */
    Object process(
        Container container,
        Object instance,
        InjectContext context,
        ConstructorContext constructor
    );
}
