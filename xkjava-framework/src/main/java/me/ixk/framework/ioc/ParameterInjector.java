/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

@FunctionalInterface
public interface ParameterInjector {
    default boolean matches(
        Binding binding,
        Executable executable,
        Parameter[] parameters,
        String[] parameterNames,
        Object[] dependencies
    ) {
        return true;
    }

    default Object[] process(
        Container container,
        Binding binding,
        Executable method,
        Parameter[] parameters,
        String[] parameterNames,
        Object[] dependencies,
        DataBinder dataBinder
    ) {
        if (
            this.matches(
                    binding,
                    method,
                    parameters,
                    parameterNames,
                    dependencies
                )
        ) {
            return this.inject(
                    container,
                    binding,
                    method,
                    parameters,
                    parameterNames,
                    dependencies,
                    dataBinder
                );
        }
        return dependencies;
    }

    Object[] inject(
        Container container,
        Binding binding,
        Executable method,
        Parameter[] parameters,
        String[] parameterNames,
        Object[] dependencies,
        DataBinder dataBinder
    );
}
