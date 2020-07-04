/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import static me.ixk.framework.helpers.FacadeHelper.auth;
import static me.ixk.framework.helpers.FacadeHelper.response;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RouteMiddleware;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

@RouteMiddleware(name = "auth")
@Order(Order.MEDIUM_PRECEDENCE)
public class Authenticate implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        if (auth().guest()) {
            return response().redirect("/login");
        }
        return next.handle(request);
    }
}
