/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.middleware;

import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.middleware.HandlerMiddlewareChain;
import me.ixk.framework.middleware.Middleware;

public class Middleware2 implements Middleware {

    @Override
    public Object handle(
        final Request request,
        final Response response,
        final HandlerMiddlewareChain next
    ) throws Exception {
        System.out.println("Middleware2");
        return next.handle(request, response);
    }
}
