/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.entity.InjectContext;

/**
 * 实例注入器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:38
 */
public interface InstanceInjector {
    /**
     * 是否匹配注入器
     *
     * @param context  实例上下文
     * @param instance 实例
     *
     * @return 是否匹配
     */
    boolean supportsInstance(final InjectContext context, Object instance);

    /**
     * 处理
     *
     * @param container 容器
     * @param instance  实例
     * @param context   参数上下文
     *
     * @return 实例
     */
    default Object process(
        final Container container,
        final Object instance,
        final InjectContext context
    ) {
        if (this.supportsInstance(context, instance)) {
            return this.inject(container, instance, context);
        }
        return instance;
    }

    /**
     * 注入
     *
     * @param container 容器
     * @param instance  实例
     * @param context   参数上下文
     *
     * @return 实例
     */
    Object inject(Container container, Object instance, InjectContext context);
}
