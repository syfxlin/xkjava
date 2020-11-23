/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.route.RouteResult;

/**
 * 处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:43
 */
@FunctionalInterface
public interface Handler {
    /**
     * 前置处理器
     *
     * @param result   路由信息
     * @param request  请求
     * @param response 响应
     */
    default void before(
        RouteResult result,
        Request request,
        Response response
    ) {}

    /**
     * 处理
     *
     * @param request  请求对象
     * @param response 响应对象
     *
     * @return 返回值
     */
    Object handle(Request request, Response response);

    /**
     * 后置处理器
     *
     * @param response         响应对象
     * @param request          请求
     * @param originalResponse 响应
     */
    default void after(
        Response response,
        Request request,
        Response originalResponse
    ) {}

    /**
     * 后置异常处理器
     *
     * @param e        响应对象
     * @param request  请求
     * @param response 响应
     *
     * @return 是否解决了异常
     */
    default Response afterException(
        Throwable e,
        Request request,
        Response response
    ) {
        return null;
    }

    /**
     * 后置正常处理器
     *
     * @param response         响应对象
     * @param request          请求
     * @param originalResponse 响应
     *
     * @return 响应对象
     */
    default Response afterReturning(
        Response response,
        Request request,
        Response originalResponse
    ) {
        return response;
    }
}
