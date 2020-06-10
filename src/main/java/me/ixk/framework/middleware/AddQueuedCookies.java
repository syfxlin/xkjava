package me.ixk.framework.middleware;

import me.ixk.framework.facades.App;
import me.ixk.framework.http.CookieManager;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SetCookie;

public class AddQueuedCookies implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        Response response = next.handle(request);
        SetCookie[] cookies = App
            .make(CookieManager.class)
            .getQueues()
            .values()
            .toArray(SetCookie[]::new);
        return response.addCookies(cookies);
    }
}
