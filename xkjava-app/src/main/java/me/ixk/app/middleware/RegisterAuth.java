package me.ixk.app.middleware;

import me.ixk.app.auth.Auth;
import me.ixk.framework.annotations.GlobalMiddleware;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.middleware.Runner;

@GlobalMiddleware
@Order(Order.HIGHEST_PRECEDENCE + 100)
public class RegisterAuth implements Middleware {

    @Override
    public Response handle(final Request request, final Runner next) {
        XkJava.of().instance(Auth.class, new Auth(), "auth", ScopeType.REQUEST);
        return next.handle(request);
    }
}
