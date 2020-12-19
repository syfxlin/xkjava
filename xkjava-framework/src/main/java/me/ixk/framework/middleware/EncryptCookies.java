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
import me.ixk.framework.route.RouteInfo;

/**
 * 加密 Cookie
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:42
 */
@GlobalMiddleware
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class EncryptCookies implements Middleware {

    @Override
    public Object handle(
        Request request,
        Response response,
        MiddlewareChain next,
        RouteInfo info
    ) {
        this.decrypt(request);
        Object value = next.handle(request, response);
        this.encrypt(response);
        return value;
    }

    protected void decrypt(final Request request) {
        final Cookie[] cookies = request.getCookies();
        for (final Cookie cookie : cookies) {
            final String decrypt = crypt().decrypt(cookie.getValue());
            if (decrypt != null) {
                cookie.setValue(decrypt);
            }
        }
    }

    protected void encrypt(final Response response) {
        final List<SetCookie> cookies = response.getCookies();
        for (final SetCookie cookie : cookies) {
            if (cookie.isEncrypt()) {
                cookie.setValue(crypt().encrypt(cookie.getValue()));
            }
        }
    }
}
