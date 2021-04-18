/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import java.util.concurrent.ConcurrentMap;

/**
 * Context
 *
 * @author Otstar Lin
 * @date 2020/10/25 下午 9:27
 */
public interface Context {
    /**
     * 是否是共享的，即单例
     *
     * @return 是否
     */
    default boolean isShared() {
        return true;
    }

    /**
     * 该 Context 是否启动，一般的 Context 只要 new 后就会启动 但是如果是 ThreadLocal 则需要另行启动
     *
     * @return 是否启动
     */
    default boolean isCreated() {
        return true;
    }

    /**
     * 是否需要代理
     *
     * @return 是否需要代理
     */
    default boolean useProxy() {
        return false;
    }

    /**
     * 获取所有实例
     *
     * @return 所有实例
     */
    ConcurrentMap<String, Object> getInstances();

    /**
     * 获取实例
     *
     * @param name 实例名称
     * @return 实例
     */
    default Object get(final String name) {
        return this.getInstances().get(name);
    }

    /**
     * 删除实例
     *
     * @param name 实例名称
     */
    default void remove(final String name) {
        this.getInstances().remove(name);
    }

    /**
     * 设置实例
     *
     * @param name     名称
     * @param instance 实例
     */
    default void set(final String name, final Object instance) {
        this.getInstances().put(name, instance);
    }

    /**
     * 是否存在实例
     *
     * @param name 实例名称
     * @return 是否存在
     */
    default boolean has(final String name) {
        return this.getInstances().containsKey(name);
    }
}
