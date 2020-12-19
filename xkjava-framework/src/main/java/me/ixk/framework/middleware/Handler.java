/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.route.RouteInfo;

/**
 * 处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:43
 */
public interface Handler {
    /**
     * 前置处理器
     *
     * @param request  请求
     * @param response 响应
     * @param result   路由信息
     */
    void before(Request request, Response response, RouteInfo result);

    /**
     * 处理
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param info     路由信息
     *
     * @return 返回值
     */
    Object handle(Request request, Response response, RouteInfo info);

    /**
     * 后置异常处理器
     *
     * @param e        响应对象
     * @param request  请求
     * @param response 响应
     * @param info     路由信息
     *
     * @return 是否解决了异常
     */
    Response afterException(
        Throwable e,
        Request request,
        Response response,
        RouteInfo info
    );

    /**
     * 后置正常处理器
     *
     * @param returnValue 响应对象
     * @param request     请求
     * @param response    响应
     * @param info        路由信息
     *
     * @return 响应对象
     */
    Response afterReturning(
        Object returnValue,
        Request request,
        Response response,
        RouteInfo info
    );
}
