package me.ixk.framework.ioc.injector;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.ParameterInjector;
import me.ixk.framework.ioc.With;
import me.ixk.framework.utils.ParameterNameDiscoverer;

public class DefaultParameterInjector implements ParameterInjector {

    @Override
    public Object[] inject(Container container, Executable method, With with) {
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = ParameterNameDiscoverer.getParameterNames(
            method
        );
        Object[] dependencies = new Object[parameters.length];
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
