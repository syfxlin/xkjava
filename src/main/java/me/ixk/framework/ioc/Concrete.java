package me.ixk.framework.ioc;

import java.util.Map;

@FunctionalInterface
public interface Concrete {
    Object getObject(Container container, Map<String, Object> args)
        throws Throwable;
}
