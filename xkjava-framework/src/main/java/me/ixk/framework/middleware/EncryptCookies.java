/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import static me.ixk.framework.helpers.Facade.crypt;

import java.util.List;
import javax.servlet.http.Cookie;
import me.ixk.framework.annotations.GlobalMiddleware;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SetCookie;

@GlobalMiddleware
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class EncryptCookies implements Middleware {

    @Override
    public Response handle(final Request request, final Runner next) {
        return this.encrypt(next.handle(this.decrypt(request)));
    }

    protected Request decrypt(final Request request) {
        final Cookie[] cookies = request.getCookies();
        for (final Cookie cookie : cookies) {
            final String decrypt = crypt().decrypt(cookie.getValue());
            if (decrypt != null) {
                cookie.setValue(decrypt);
            }
        }
        return request;
    }

    protected Response encrypt(final Response response) {
        final List<SetCookie> cookies = response.getCookies();
        for (final SetCookie cookie : cookies) {
            if (cookie.isEncrypt()) {
                cookie.setValue(crypt().encrypt(cookie.getValue()));
            }
        }
        return response;
    }
}
