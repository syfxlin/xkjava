/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RouteMiddleware;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

import static me.ixk.framework.helpers.Facade.auth;
import static me.ixk.framework.helpers.Facade.response;

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
