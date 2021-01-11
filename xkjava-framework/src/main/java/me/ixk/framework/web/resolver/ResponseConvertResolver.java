/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.web.WebContext;

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
     * @param value   返回值
     * @param context Web 上下文
     * @param info    路由信息
     * @return 是否支持
     */
    boolean supportsConvert(Object value, WebContext context, RouteInfo info);

    /**
     * 解析返回值
     *
     * @param value   返回值
     * @param context Web 上下文
     * @param info    路由信息
     * @return 返回值
     */
    boolean resolveConvert(Object value, WebContext context, RouteInfo info);
}
