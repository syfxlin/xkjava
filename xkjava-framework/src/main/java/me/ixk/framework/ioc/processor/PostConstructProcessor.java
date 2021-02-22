/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import java.lang.reflect.Method;
import me.ixk.framework.annotation.core.BeanProcessor;
import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.entity.ConstructorContext;
import me.ixk.framework.ioc.entity.InjectContext;

/**
 * 构造器后处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:07
 */
@BeanProcessor
@Order(Order.HIGHEST_PRECEDENCE)
public class PostConstructProcessor implements BeanAfterCreateProcessor {

    @Override
    public Object process(
        final Container container,
        final Object instance,
        final InjectContext context,
        final ConstructorContext constructor
    ) {
        for (final Method initMethod : context.getBinding().getInitMethods()) {
            container.call(instance, initMethod, Object.class);
        }
        return instance;
    }
}
