package me.ixk.framework.ioc.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Otstar Lin
 * @date 2021/4/18 下午 9:46
 */
public class PrototypeContext implements Context {

    private static final ConcurrentMap<String, Object> EMPTY = new ConcurrentHashMap<>();

    @Override
    public ConcurrentMap<String, Object> getInstances() {
        return EMPTY;
    }

    @Override
    public boolean isShared() {
        return false;
    }
}
