/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import java.lang.reflect.Method;
import me.ixk.framework.annotations.BeanProcessor;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.entity.InjectContext;

/**
 * 销毁前处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:07
 */
@BeanProcessor
@Order(Order.LOWEST_PRECEDENCE)
public class PreDestroyProcessor implements BeanDestroyProcessor {

    @Override
    public void process(
        Container container,
        Object instance,
        InjectContext context
    ) {
        for (Method destroyMethod : context.getBinding().getDestroyMethods()) {
            container.call(instance, destroyMethod, Object.class);
        }
    }
}
