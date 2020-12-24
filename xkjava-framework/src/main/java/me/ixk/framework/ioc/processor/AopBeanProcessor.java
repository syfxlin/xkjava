/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import me.ixk.framework.annotations.BeanProcessor;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.ProxyCreator;
import me.ixk.framework.bootstrap.LoadEnvironmentVariables;
import me.ixk.framework.ioc.ConstructorContext;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.InstanceContext;
import me.ixk.framework.utils.ClassUtils;

/**
 * Aop 实例前置处理器
 *
 * @author Otstar Lin
 * @date 2020/12/24 下午 2:34
 */
@BeanProcessor
@Order(Order.LOWEST_PRECEDENCE)
public class AopBeanProcessor implements BeanBeforeProcessor {

    @Override
    public Object process(
        Container container,
        Object instance,
        InstanceContext context,
        ConstructorContext constructor
    ) {
        final Class<?> instanceType = context.getInstanceType();
        if (this.aspectMatches(instanceType, container)) {
            return ProxyCreator.createAop(
                container.make(AspectManager.class),
                instance,
                instanceType,
                instanceType.getInterfaces(),
                constructor.getConstructor().getParameterTypes(),
                constructor.getArgs()
            );
        } else {
            return instance;
        }
    }

    protected boolean aspectMatches(final Class<?> type, Container container) {
        // Disable proxy Advice and AspectManager
        if (
            Advice.class.isAssignableFrom(type) || type == AspectManager.class
        ) {
            return false;
        }
        // Disable some bootstrap
        if (type == LoadEnvironmentVariables.class) {
            return false;
        }
        if (ClassUtils.isSkipBuildType(type)) {
            return false;
        }
        final AspectManager aspectManager = container.make(AspectManager.class);
        if (aspectManager == null) {
            return false;
        }
        return aspectManager.matches(type);
    }
}
