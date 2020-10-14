/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

/**
 * 可响应
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:01
 */
public interface Responsable {
    /**
     * 转换成响应
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param result   返回值
     *
     * @return 响应对象
     */
    Response toResponse(Request request, Response response, Object result);
}
