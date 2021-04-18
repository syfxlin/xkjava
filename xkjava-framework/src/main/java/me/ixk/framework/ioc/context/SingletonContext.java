/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SingletonContext
 * <p>
 * 保存 App 全局共享的 Bean
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:39
 */
public class SingletonContext implements Context {

    private final ConcurrentMap<String, Object> instances = new ConcurrentHashMap<>();

    @Override
    public ConcurrentMap<String, Object> getInstances() {
        return this.instances;
    }

    @Override
    public boolean isShared() {
        return true;
    }
}
