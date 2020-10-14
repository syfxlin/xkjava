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
        Container container,
        Binding binding,
        Object instance,
        Class<?> instanceClass,
        DataBinder dataBinder
    ) {
        if (instance == null) {
            return null;
        }
        List<Method> methods = binding.getAutowiredMethods();
        for (Method method : methods) {
            // Set 注入
            container.call(instance, method, Object.class, dataBinder);
        }
        return instance;
    }
}
