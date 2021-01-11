/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import java.io.IOException;

/**
 * 可响应
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:01
 */
@FunctionalInterface
public interface Responsible {
    /**
     * 转换成响应
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param result   返回值
     * @return 响应对象
     * @throws IOException IO 异常
     */
    boolean toResponse(Request request, Response response, Object result)
        throws IOException;
}
