package me.ixk.framework.newioc.injector;

import cn.hutool.core.convert.Convert;
import me.ixk.framework.newioc.Container;
import me.ixk.framework.newioc.ParameterInjector;
import me.ixk.framework.utils.ParameterNameDiscoverer;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Map;

public class DefaultParameterInjector implements ParameterInjector {

    @Override
    public Object[] inject(
        Container container,
        Executable method,
        Map<String, Object> args
    ) {
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = ParameterNameDiscoverer.getParameterNames(
            method
        );
        Object[] dependencies = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (
                args.containsKey(parameter.getType().getName()) ||
                args.containsKey(
                    parameterNames[i] != null
                        ? parameterNames[i]
                        : parameter.getName()
                )
            ) {
                dependencies[i] = args.get(parameterNames[i]);
            } else {
                Class<?> _class = parameter.getType();
                dependencies[i] = container.make(_class.getName(), _class);
            }
            dependencies[i] =
                Convert.convert(parameter.getType(), dependencies[i]);
        }
        return dependencies;
    }
}
