/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

/**
 * RequestParametersPostResolver
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:20
 */
public interface RequestParametersPostResolver {
    /**
     * 是否支持
     *
     * @param parameters 参数值
     * @param parameter  参数信息
     * @param context    Web 上下文
     * @param binder     数据绑定器
     *
     * @return 是否支持
     */
    boolean supportsParameters(
        Object[] parameters,
        MethodParameter parameter,
        WebContext context,
        WebDataBinder binder
    );

    /**
     * 解析参数
     *
     * @param parameters 参数值
     * @param parameter  参数信息
     * @param context    Web 上下文
     * @param binder     数据绑定器
     *
     * @return 参数值
     */
    Object[] resolveParameters(
        Object[] parameters,
        MethodParameter parameter,
        WebContext context,
        WebDataBinder binder
    );
}
