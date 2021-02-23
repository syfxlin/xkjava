/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import java.util.Map;
import javax.servlet.http.Cookie;
import me.ixk.framework.http.HttpHeader;
import me.ixk.framework.http.HttpHeaders;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Renderable;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SetCookie;
import me.ixk.framework.ioc.XkJava;

/**
 * HTTP 响应
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:10
 */
public abstract class AbstractHttpResult implements Renderable {

    protected final Response response = XkJava.of().make(Response.class);

    public Response response() {
        return response;
    }

    public final AbstractHttpResult status(final int sc) {
        response.status(sc);
        return this;
    }

    public final AbstractHttpResult status(final HttpStatus status) {
        response.status(status);
        return this;
    }

    public final AbstractHttpResult header(
        final String name,
        final String value
    ) {
        response.header(name, value);
        return this;
    }

    public final AbstractHttpResult header(
        final HttpHeader name,
        final String value
    ) {
        response.header(name, value);
        return this;
    }

    public final AbstractHttpResult headers(final Map<Object, String> headers) {
        response.headers(headers);
        return this;
    }

    public final AbstractHttpResult headers(final HttpHeaders headers) {
        response.headers(headers);
        return this;
    }

    public final AbstractHttpResult cookie(final Cookie cookie) {
        response.cookie(cookie);
        return this;
    }

    public final AbstractHttpResult cookie(final SetCookie cookie) {
        response.cookie(cookie);
        return this;
    }

    public final AbstractHttpResult cookie(
        final String name,
        final String value,
        final String domain,
        final String path,
        final int maxAge,
        final String comment,
        final boolean isSecure,
        final boolean isHttpOnly,
        final int version
    ) {
        response.cookie(
            name,
            value,
            domain,
            path,
            maxAge,
            comment,
            isSecure,
            isHttpOnly,
            version
        );
        return this;
    }
}
