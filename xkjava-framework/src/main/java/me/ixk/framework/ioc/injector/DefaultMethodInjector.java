/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.utils.AnnotationUtils;

/**
 * 默认的方法注入器
 * <p>
 * 执行被 @Autowired 标注的方法
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:43
 */
public class DefaultMethodInjector implements InstanceInjector {

    @Override
    public Object inject(
        final Container container,
        final Binding binding,
        final Object instance,
        final Class<?> instanceClass,
        final DataBinder dataBinder
    ) {
        if (AnnotationUtils.isSkipped(instanceClass, this.getClass())) {
            return instance;
        }
        final List<Method> methods = binding.getAutowiredMethods();
        for (final Method method : methods) {
            if (AnnotationUtils.isSkipped(method, this.getClass())) {
                continue;
            }
            // Set 注入
            container.call(instance, method, Object.class, dataBinder);
        }
        return instance;
    }
}
