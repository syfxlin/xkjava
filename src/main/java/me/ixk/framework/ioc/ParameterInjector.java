/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.lang.reflect.Executable;

@FunctionalInterface
public interface ParameterInjector {
    default boolean matches(
        Binding binding,
        Executable executable,
        Object[] dependencies
    ) {
        return true;
    }

    default Object[] process(
        Container container,
        Binding binding,
        Executable method,
        Object[] dependencies,
        DataBinder dataBinder
    ) {
        if (this.matches(binding, method, dependencies)) {
            return this.inject(container, binding, method, dependencies, dataBinder);
        }
        return dependencies;
    }

    Object[] inject(
        Container container,
        Binding binding,
        Executable method,
        Object[] dependencies,
        DataBinder dataBinder
    );
}
