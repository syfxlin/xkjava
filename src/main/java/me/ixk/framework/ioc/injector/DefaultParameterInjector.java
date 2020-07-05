/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.ParameterInjector;
import me.ixk.framework.ioc.With;
import me.ixk.framework.utils.ParameterNameDiscoverer;

public class DefaultParameterInjector
    extends AbstractInjector
    implements ParameterInjector {

    public DefaultParameterInjector(Container container) {
        super(container);
    }

    @Override
    public Object[] inject(
        Binding binding,
        Executable method,
        Object[] dependencies,
        With with
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
                container.getInjectValue(
                    parameter.getType(),
                    parameterName,
                    with
                );
        }
        return dependencies;
    }
}
