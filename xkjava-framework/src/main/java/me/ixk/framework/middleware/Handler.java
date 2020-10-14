/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.http.Request;

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
     * @param request 请求对象
     *
     * @return 返回值
     */
    Object handle(Request request);
}
