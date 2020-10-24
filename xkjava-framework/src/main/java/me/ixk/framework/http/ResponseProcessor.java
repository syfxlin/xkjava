/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.utils.JSON;

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
        } else if (result instanceof Responsable) {
            // 可响应则执行转换响应方法
            return ((Responsable) result).toResponse(request, response, result);
        } else if (result instanceof String) {
            // 如果是字符串，则直接插入到响应中
            return response.content(result.toString());
        } else {
            // 其他对象则序列号成 JSON
            return response
                .contentType(MimeType.APPLICATION_JSON.asString())
                .content(JSON.stringify(result));
        }
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
