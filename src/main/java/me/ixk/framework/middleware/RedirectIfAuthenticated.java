package me.ixk.framework.middleware;

import me.ixk.framework.facades.Auth;
import me.ixk.framework.facades.Resp;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

public class RedirectIfAuthenticated implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        if (Auth.check()) {
            return Resp.redirect("/home");
        }
        return next.handle(request);
    }
}
