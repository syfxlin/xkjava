/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

/**
 * 中间件
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:43
 */
public interface Middleware {
    /**
     * 前置处理
     *
     * @param request  请求对象
     * @param response 响应对象
     * @return 是否继续执行
     * @throws Exception 异常
     */
    default boolean beforeHandle(Request request, Response response)
        throws Exception {
        return true;
    }

    /**
     * 后置处理
     *
     * @param returnValue 返回值
     * @param request     请求对象
     * @param response    相应对象
     * @return 返回值
     * @throws Exception 异常
     */
    default Object afterHandle(
        Object returnValue,
        Request request,
        Response response
    ) throws Exception {
        return returnValue;
    }

    /**
     * 后置完成处理
     *
     * @param request  请求对象
     * @param response 响应对象
     * @throws Exception 异常
     */
    default void afterCompletion(Request request, Response response)
        throws Exception {}
}
