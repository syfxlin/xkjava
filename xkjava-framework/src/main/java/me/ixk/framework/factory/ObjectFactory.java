/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.factory;

/**
 * 对象工厂
 * <p>
 * 通过代理动态获取对象，用于注入到单例的时候保持对象的线程安全
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:07
 */
@FunctionalInterface
public interface ObjectFactory<T> {
    /**
     * 获取实例
     *
     * @return 实例
     */
    T getObject();
}
