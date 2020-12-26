/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.entity.InjectContext;

/**
 * Bean 销毁处理器
 * <p>
 * 在 Bean 销毁前执行，instance 绑定的 Bean 无法被处理
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:07
 */
public interface BeanDestroyProcessor {
    /**
     * 处理
     *
     * @param container 容器
     * @param instance  Bean 实例
     * @param context   实例上下文
     */
    void process(Container container, Object instance, InjectContext context);
}
