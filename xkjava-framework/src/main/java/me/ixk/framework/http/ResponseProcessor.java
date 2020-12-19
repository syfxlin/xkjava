/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

/**
 * 响应处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:14
 */
public class ResponseProcessor {

    /**
     * 响应前操作
     *
     * @param response 响应对象
     *
     * @return 响应对象
     */
    public static Response dispatchResponse(final Response response) {
        // 推送 Cookie Queue 到响应中
        return response.pushCookieToHeader();
    }
}
