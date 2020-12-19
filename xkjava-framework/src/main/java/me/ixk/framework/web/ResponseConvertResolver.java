/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import me.ixk.framework.http.Response;
import me.ixk.framework.route.RouteInfo;

/**
 * 响应转换器
 *
 * @author Otstar Lin
 * @date 2020/12/19 下午 8:06
 */
public interface ResponseConvertResolver {
    /**
     * 是否支持
     *
     * @param value       返回值
     * @param returnValue 返回值信息
     * @param context     Web 上下文
     * @param info        路由信息
     *
     * @return 是否支持
     */
    boolean supportsConvert(
        Object value,
        MethodReturnValue returnValue,
        WebContext context,
        RouteInfo info
    );

    /**
     * 解析返回值
     *
     * @param value       返回值
     * @param returnValue 返回值信息
     * @param context     Web 上下文
     * @param info        路由信息
     *
     * @return 返回值
     */
    Response resolveConvert(
        Object value,
        MethodReturnValue returnValue,
        WebContext context,
        RouteInfo info
    );
}
