/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.factory;

/**
 * Object 工厂
 *
 * @author Otstar Lin
 * @date 2020/12/23 下午 9:56
 */
@FunctionalInterface
public interface ObjectFactory<T> {
    /**
     * 获取对象
     *
     * @return 获取对象
     */
    T getObject();
}
