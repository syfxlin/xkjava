package me.ixk.framework.middleware;

import java.io.IOException;
import me.ixk.framework.facades.Auth;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

public class Authenticate implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        if (Auth.guest()) {
            try {
                return me.ixk.framework.facades.Response.redirect("/login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return next.handle(request);
    }
}
