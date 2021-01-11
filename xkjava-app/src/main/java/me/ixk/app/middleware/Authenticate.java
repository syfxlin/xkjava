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

@RouteMiddleware(name = "auth")
@Order(Order.MEDIUM_PRECEDENCE)
public class Authenticate implements Middleware {

    @Override
    public boolean beforeHandle(final Request request, final Response response)
        throws Exception {
        if (XkJava.of().make(Auth.class).guest()) {
            response().redirect("/login");
            return false;
        }
        return true;
    }
}
