/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.annotations.GlobalMiddleware;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.http.CookieManager;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SetCookie;
import me.ixk.framework.ioc.XkJava;

/**
 * 添加队列 Cookie 到 Response
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:41
 */
@GlobalMiddleware
@Order(Order.HIGHEST_PRECEDENCE + 3)
public class AddQueuedCookies implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        Response response = next.handle(request);
        SetCookie[] cookies = XkJava
            .of()
            .make(CookieManager.class)
            .getQueues()
            .values()
            .toArray(SetCookie[]::new);
        return response.cookies(cookies);
    }
}
