/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

/**
 * 实例注入器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:38
 */
@FunctionalInterface
public interface InstanceInjector {
    /**
     * 是否匹配注入器
     *
     * @param binding       Binding
     * @param instance      实例
     * @param instanceClass 实例类型
     *
     * @return 是否匹配
     */
    default boolean matches(
        Binding binding,
        Object instance,
        Class<?> instanceClass
    ) {
        return instance != null;
    }

    /**
     * 处理
     *
     * @param container     容器
     * @param binding       Binding
     * @param instance      实例
     * @param instanceClass 实例类型
     * @param dataBinder    数据绑定器
     *
     * @return 实例
     */
    default Object process(
        Container container,
        Binding binding,
        Object instance,
        Class<?> instanceClass,
        DataBinder dataBinder
    ) {
        if (this.matches(binding, instance, instanceClass)) {
            return this.inject(
                    container,
                    binding,
                    instance,
                    instanceClass,
                    dataBinder
                );
        }
        return instance;
    }

    /**
     * 注入
     *
     * @param container     容器
     * @param binding       Binding
     * @param instance      实例
     * @param instanceClass 实例类型
     * @param dataBinder    数据绑定器
     *
     * @return 实例
     */
    Object inject(
        Container container,
        Binding binding,
        Object instance,
        Class<?> instanceClass,
        DataBinder dataBinder
    );
}
