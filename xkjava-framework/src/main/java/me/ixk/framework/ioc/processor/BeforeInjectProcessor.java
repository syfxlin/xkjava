/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.entity.InjectContext;

/**
 * Bean 创建前置处理器
 *
 * @author Otstar Lin
 * @date 2020/12/26 下午 7:22
 */
public interface BeforeInjectProcessor {
    /**
     * 处理
     *
     * @param container 容器
     * @param context   上下文
     */
    void process(Container container, InjectContext context);
}
