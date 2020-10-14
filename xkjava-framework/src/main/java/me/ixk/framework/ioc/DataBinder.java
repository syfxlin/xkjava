/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import me.ixk.framework.annotations.DataBind;

/**
 * 数据绑定器
 * <p>
 * 用于获取对应名称或类型的 Bean
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:36
 */
public interface DataBinder {
    /**
     * 获取实例
     *
     * @param name 实例名
     * @param type 实例类型
     * @param <T>  实例类型
     *
     * @return 实例
     */
    <T> T getObject(String name, Class<T> type);

    /**
     * 获取实例
     *
     * @param name     实例名
     * @param type     实例类型
     * @param dataBind 数据绑定器
     * @param <T>      实例类型
     *
     * @return 实例
     */
    <T> T getObject(String name, Class<T> type, DataBind dataBind);
}
