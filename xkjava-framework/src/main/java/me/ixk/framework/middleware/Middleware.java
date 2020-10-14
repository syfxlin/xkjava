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
@FunctionalInterface
public interface Middleware {
    /**
     * 处理
     *
     * @param request 请求对象
     * @param next    中间件执行链
     *
     * @return 响应对象
     */
    Response handle(Request request, Runner next);
}
