package me.ixk.framework.ioc.injector;

import cn.hutool.core.convert.Convert;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Map;
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
            Map<String, Object> withMap = with.getMap();
            if (
                withMap.containsKey(parameter.getType().getName()) ||
                withMap.containsKey(
                    parameterNames[i] != null
                        ? parameterNames[i]
                        : parameter.getName()
                )
            ) {
                dependencies[i] = withMap.get(parameterNames[i]);
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
