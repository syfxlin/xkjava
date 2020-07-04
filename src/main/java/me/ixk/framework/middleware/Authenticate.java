/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RouteMiddleware;
import me.ixk.framework.facades.Auth;
import me.ixk.framework.facades.Resp;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

@RouteMiddleware(name = "auth")
@Order(Order.MEDIUM_PRECEDENCE)
public class Authenticate implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        if (Auth.guest()) {
            return Resp.redirect("/login");
        }
        return next.handle(request);
    }
}
