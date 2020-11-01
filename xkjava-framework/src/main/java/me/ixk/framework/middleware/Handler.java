/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

/**
 * 处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:43
 */
@FunctionalInterface
public interface Handler {
    /**
     * 处理
     *
     * @param request  请求对象
     * @param response 响应对象
     *
     * @return 返回值
     */
    Object handle(Request request, Response response);
}
