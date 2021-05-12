/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

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
     * 获取实例
     *
     * @param name 实例名称
     * @return 实例
     */
    Object get(final String name);

    /**
     * 删除实例
     *
     * @param name 实例名称
     */
    void remove(final String name);

    /**
     * 设置实例
     *
     * @param name     名称
     * @param instance 实例
     */
    void set(final String name, final Object instance);

    /**
     * 是否存在实例
     *
     * @param name 实例名称
     * @return 是否存在
     */
    boolean has(final String name);
}
