package me.ixk.framework.ioc;

import java.util.Map;

@FunctionalInterface
public interface Wrapper {
    Object getInstance(Container container, Map<String, Object> args)
        throws Throwable;
}
