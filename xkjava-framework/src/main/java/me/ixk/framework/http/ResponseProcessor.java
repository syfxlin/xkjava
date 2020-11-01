/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.exceptions.ResponseException;

/**
 * 响应处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:14
 */
public class ResponseProcessor {

    /**
     * 将返回值转化成响应
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param result   返回值
     *
     * @return 响应对象
     */
    public static Response toResponse(
        Request request,
        Response response,
        Object result
    ) {
        if (result == null) {
            // null
            return response;
        } else if (result instanceof Response) {
            // 本身就是 Response
            return response;
        } else if (result instanceof HttpServletResponse) {
            // 如果是 HttpServletResponse 则包装一下
            response.setResponse((HttpServletResponse) result);
            return response;
        }
        throw new ResponseException(
            "The return value cannot be converted into a response. [" +
            result.getClass() +
            "]"
        );
    }

    /**
     * 响应前操作
     *
     * @param response 响应对象
     *
     * @return 响应对象
     */
    public static Response dispatchResponse(Response response) {
        // 推送 Cookie Queue 到响应中
        return response.pushCookieToHeader();
    }
}
