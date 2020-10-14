/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

/**
 * 参数注入器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 12:36
 */
@FunctionalInterface
public interface ParameterInjector {
    /**
     * 是否匹配注入器
     *
     * @param binding        Binding
     * @param executable     方法
     * @param parameters     参数
     * @param parameterNames 参数名称
     * @param dependencies   参数值
     *
     * @return 是否匹配
     */
    default boolean matches(
        Binding binding,
        Executable executable,
        Parameter[] parameters,
        String[] parameterNames,
        Object[] dependencies
    ) {
        return true;
    }

    /**
     * 处理
     *
     * @param container      容器
     * @param binding        Binding
     * @param method         方法
     * @param parameters     参数
     * @param parameterNames 参数名称
     * @param dependencies   参数值
     * @param dataBinder     数据绑定器
     *
     * @return 参数值
     */
    default Object[] process(
        Container container,
        Binding binding,
        Executable method,
        Parameter[] parameters,
        String[] parameterNames,
        Object[] dependencies,
        DataBinder dataBinder
    ) {
        if (
            this.matches(
                    binding,
                    method,
                    parameters,
                    parameterNames,
                    dependencies
                )
        ) {
            return this.inject(
                    container,
                    binding,
                    method,
                    parameters,
                    parameterNames,
                    dependencies,
                    dataBinder
                );
        }
        return dependencies;
    }

    /**
     * 注入
     *
     * @param container      容器
     * @param binding        Binding
     * @param method         方法
     * @param parameters     参数
     * @param parameterNames 参数名称
     * @param dependencies   参数值
     * @param dataBinder     数据绑定器
     *
     * @return 参数值
     */
    Object[] inject(
        Container container,
        Binding binding,
        Executable method,
        Parameter[] parameters,
        String[] parameterNames,
        Object[] dependencies,
        DataBinder dataBinder
    );
}
