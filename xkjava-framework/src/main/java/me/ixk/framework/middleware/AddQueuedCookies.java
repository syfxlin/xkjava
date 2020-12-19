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
import me.ixk.framework.route.RouteInfo;

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
    public Object handle(
        Request request,
        Response response,
        MiddlewareChain next,
        RouteInfo info
    ) {
        Object value = next.handle(request, response);
        SetCookie[] cookies = XkJava
            .of()
            .make(CookieManager.class)
            .getQueues()
            .values()
            .toArray(SetCookie[]::new);
        response.cookies(cookies);
        return value;
    }
}
