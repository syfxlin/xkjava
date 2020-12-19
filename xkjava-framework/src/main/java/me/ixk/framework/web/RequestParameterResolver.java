/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

/**
 * RequestParameterResolver
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:18
 */
public interface RequestParameterResolver {
    /**
     * 是否支持
     *
     * @param value     参数值
     * @param parameter 参数信息
     * @param context   Web 上下文
     * @param binder    数据绑定器
     *
     * @return 是否支持
     */
    boolean supportsParameter(
        Object value,
        MethodParameter parameter,
        WebContext context,
        WebDataBinder binder
    );

    /**
     * 解析参数
     *
     * @param value     参数值
     * @param parameter 参数信息
     * @param context   Web 上下文
     * @param binder    数据绑定器
     *
     * @return 参数值
     */
    Object resolveParameter(
        Object value,
        MethodParameter parameter,
        WebContext context,
        WebDataBinder binder
    );
}
