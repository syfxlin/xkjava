package me.ixk.framework.newioc;

import java.util.Map;

@FunctionalInterface
public interface MethodInjector {
    Object inject(
        Container container,
        Object instance,
        Map<String, Object> args
    );
}
