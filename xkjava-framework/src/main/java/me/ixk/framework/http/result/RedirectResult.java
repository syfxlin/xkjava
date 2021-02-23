/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

/**
 * 跳转响应
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:11
 */
public class RedirectResult extends AbstractHttpResult {

    protected String url;

    public RedirectResult(final String url) {
        this(url, HttpStatus.FOUND);
    }

    public RedirectResult(final String url, final int status) {
        this.url = url;
        this.status(status);
    }

    public RedirectResult(final String url, final HttpStatus status) {
        this.url = url;
        this.status(status);
    }

    public RedirectResult url(final String url) {
        this.url = url;
        return this;
    }

    public String url() {
        return url;
    }

    public int status() {
        return this.response().getStatus();
    }

    @Override
    public String render() {
        return "";
    }

    @Override
    public boolean toResponse(
        final Request request,
        final Response response,
        final Object result
    ) {
        response.redirect(this.url, this.response().getStatus());
        return true;
    }
}
