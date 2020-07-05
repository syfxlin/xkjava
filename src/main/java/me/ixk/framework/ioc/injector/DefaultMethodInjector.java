/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.ioc.With;

public class DefaultMethodInjector implements InstanceInjector {

    @Override
    public Object inject(
        Container container,
        Binding binding,
        Object instance,
        With with
    ) {
        if (instance == null) {
            return null;
        }
        List<Method> methods = binding.getAutowiredMethods();
        for (Method method : methods) {
            // Set 注入
            container.call(
                instance,
                method,
                Object.class,
                with.getPrefix(),
                with.getMap()
            );
        }
        return instance;
    }
}
