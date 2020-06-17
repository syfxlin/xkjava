package me.ixk.framework.newioc;

import java.util.Map;

@FunctionalInterface
public interface Wrapper {
    Object getInstance(Container container, Map<String, Object> args);
}
