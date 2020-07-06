/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.ParameterInjector;
import me.ixk.framework.utils.ParameterNameDiscoverer;

public class DefaultParameterInjector implements ParameterInjector {

    @Override
    public Object[] inject(
        Container container,
        Binding binding,
        Executable method,
        Object[] dependencies,
        DataBinder dataBinder
    ) {
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = ParameterNameDiscoverer.getParameterNames(
            method
        );
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String parameterName = parameterNames[i] != null
                ? parameterNames[i]
                : parameter.getName();
            dependencies[i] =
                dataBinder.getObject(parameterName, parameter.getType());
        }
        return dependencies;
    }
}
