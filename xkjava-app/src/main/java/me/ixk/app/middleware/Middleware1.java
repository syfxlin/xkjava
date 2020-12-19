/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.middleware.MiddlewareChain;
import me.ixk.framework.route.RouteInfo;

public class Middleware1 implements Middleware {

    @Override
    public Object handle(
        Request request,
        Response response,
        MiddlewareChain next,
        RouteInfo info
    ) {
        System.out.println("Middleware1");
        return next.handle(request, response);
    }
}
