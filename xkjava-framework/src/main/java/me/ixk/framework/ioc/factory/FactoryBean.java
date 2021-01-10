/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.factory;

/**
 * Bean 生产者
 *
 * @author Otstar Lin
 * @date 2020/12/23 下午 9:02
 */
public interface FactoryBean<T> {
    /**
     * 获取对象
     *
     * @return 对象
     */
    T getObject();

    /**
     * 获取对象类型
     *
     * @return 对象类型
     */
    Class<?> getObjectType();
}
