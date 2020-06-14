package me.ixk.framework.ioc;

import java.lang.reflect.Executable;
import java.util.Map;

@FunctionalInterface
public interface ParameterInjector {
    Object[] inject(
        Container container,
        Executable method,
        Map<String, Object> args
    );
}