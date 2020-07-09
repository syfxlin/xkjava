/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

@FunctionalInterface
public interface InstanceInjector {
    default boolean matches(
        Binding binding,
        Object instance,
        Class<?> instanceClass
    ) {
        return instance != null;
    }

    default Object process(
        Container container,
        Binding binding,
        Object instance,
        Class<?> instanceClass,
        DataBinder dataBinder
    ) {
        if (this.matches(binding, instance, instanceClass)) {
            return this.inject(
                    container,
                    binding,
                    instance,
                    instanceClass,
                    dataBinder
                );
        }
        return instance;
    }

    Object inject(
        Container container,
        Binding binding,
        Object instance,
        Class<?> instanceClass,
        DataBinder dataBinder
    );
}
