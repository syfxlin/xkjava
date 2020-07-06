/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

@FunctionalInterface
public interface InstanceInjector {
    default boolean matches(Binding binding, Object instance) {
        return true;
    }

    default Object process(
        Container container,
        Binding binding,
        Object instance,
        DataBinder dataBinder
    ) {
        if (this.matches(binding, instance)) {
            return this.inject(container, binding, instance, dataBinder);
        }
        return instance;
    }

    Object inject(
        Container container,
        Binding binding,
        Object instance,
        DataBinder dataBinder
    );
}
