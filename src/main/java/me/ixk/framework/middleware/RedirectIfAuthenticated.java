package me.ixk.framework.middleware;

import java.io.IOException;
import me.ixk.framework.facades.Auth;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

public class RedirectIfAuthenticated implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        if (Auth.check()) {
            try {
                return me.ixk.framework.facades.Response.redirect("/home");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return next.handle(request);
    }
}
