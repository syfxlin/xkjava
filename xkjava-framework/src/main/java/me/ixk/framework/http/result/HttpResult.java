/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import java.util.Map;
import javax.servlet.http.Cookie;
import me.ixk.framework.http.HttpHeaders;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Renderable;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SetCookie;
import me.ixk.framework.ioc.XkJava;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpHeader;

/**
 * HTTP 响应
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:10
 */
public abstract class HttpResult implements Renderable {
    private final Response response = XkJava.of().make(Response.class);

    public final Response getResponse() {
        return response;
    }

    public final HttpResult status(int sc) {
        response.status(sc);
        return this;
    }

    public final HttpResult status(HttpStatus status) {
        response.status(status);
        return this;
    }

    public final HttpResult header(String name, String value) {
        response.header(name, value);
        return this;
    }

    public final HttpResult header(HttpHeader name, String value) {
        response.header(name, value);
        return this;
    }

    public final HttpResult headers(Map<Object, String> headers) {
        response.headers(headers);
        return this;
    }

    public final HttpResult headers(HttpHeaders headers) {
        response.headers(headers);
        return this;
    }

    public final HttpResult cookie(HttpCookie cookie) {
        response.cookie(cookie);
        return this;
    }

    public final HttpResult cookie(Cookie cookie) {
        response.cookie(cookie);
        return this;
    }

    public final HttpResult cookie(SetCookie cookie) {
        response.cookie(cookie);
        return this;
    }

    public final HttpResult cookie(
        String name,
        String value,
        String domain,
        String path,
        int maxAge,
        String comment,
        boolean isSecure,
        boolean isHttpOnly,
        int version
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
