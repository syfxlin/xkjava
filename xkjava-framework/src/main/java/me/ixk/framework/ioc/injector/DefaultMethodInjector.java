/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.annotations.Injector;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.InstanceContext;
import me.ixk.framework.ioc.InstanceInjector;

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
    public boolean supportsInstance(InstanceContext context, Object instance) {
        return !context.getBinding().getAutowiredMethods().isEmpty();
    }

    @Override
    public Object inject(
        Container container,
        Object instance,
        InstanceContext context,
        DataBinder dataBinder
    ) {
        final List<Method> methods = context.getBinding().getAutowiredMethods();
        for (final Method method : methods) {
            // Set 注入
            container.call(instance, method, Object.class, dataBinder);
        }
        return instance;
    }
}
