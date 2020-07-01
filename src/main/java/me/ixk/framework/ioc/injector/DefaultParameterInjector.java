package me.ixk.framework.ioc.injector;

import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.ParameterInjector;
import me.ixk.framework.ioc.With;
import me.ixk.framework.utils.ParameterNameDiscoverer;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

public class DefaultParameterInjector
    extends AbstractInjector
    implements ParameterInjector {

    public DefaultParameterInjector(Container container) {
        super(container);
    }

    @Override
    public Object[] inject(Binding binding, Executable method, With with) {
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
