package me.ixk.framework.ioc;

import java.lang.reflect.Executable;

@FunctionalInterface
public interface ParameterInjector {
    Object[] inject(Container container, Executable method, With with);
}
