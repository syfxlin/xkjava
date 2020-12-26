/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.annotations.Injector;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.entity.InjectContext;

/**
 * 默认的方法注入器
 * <p>
 * 执行被 @Autowired 标注的方法
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:43
 */
@Injector
@Order(Order.LOWEST_PRECEDENCE)
public class DefaultMethodInjector implements InstanceInjector {

    @Override
    public boolean supportsInstance(InjectContext context, Object instance) {
        return !context.getBinding().getAutowiredMethods().isEmpty();
    }

    @Override
    public Object inject(
        Container container,
        Object instance,
        InjectContext context
    ) {
        final List<Method> methods = context.getBinding().getAutowiredMethods();
        for (final Method method : methods) {
            // Set 注入
            container.call(instance, method, Object.class, context.getBinder());
        }
        return instance;
    }
}
