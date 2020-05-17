package me.ixk.ioc;

import java.util.Map;

@FunctionalInterface
public interface Concrete {
    Object invoke(Container container, Map<String, Object> args)
        throws Throwable;
}
