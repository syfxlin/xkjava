/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Method;
import me.ixk.framework.annotation.core.BeanProcessor;
import me.ixk.framework.annotation.core.Order;
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
        final Container container,
        final Object instance,
        final InjectContext context
    ) {
        for (final Method destroyMethod : context
            .getBinding()
            .getDestroyMethods()) {
            ReflectUtil.invoke(instance, destroyMethod);
        }
    }
}
