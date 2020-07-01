package me.ixk.framework.ioc;

import java.lang.reflect.Executable;

@FunctionalInterface
public interface ParameterInjector {
    Object[] inject(Binding binding, Executable method, With with);
}
