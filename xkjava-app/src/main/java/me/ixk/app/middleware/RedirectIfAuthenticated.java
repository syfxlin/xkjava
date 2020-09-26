/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.middleware;

import static me.ixk.framework.helpers.Facade.response;

import me.ixk.app.auth.Auth;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RouteMiddleware;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.middleware.Runner;

@RouteMiddleware(name = "guest")
@Order(Order.MEDIUM_PRECEDENCE)
public class RedirectIfAuthenticated implements Middleware {

    @Override
    public Response handle(final Request request, final Runner next) {
        if (XkJava.of().make(Auth.class).check()) {
            return response().redirect("/home");
        }
        return next.handle(request);
    }
}
