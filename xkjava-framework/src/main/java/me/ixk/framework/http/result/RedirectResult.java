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

    public RedirectResult(String url) {
        this(url, HttpStatus.FOUND);
    }

    public RedirectResult(String url, int status) {
        this.url = url;
        this.status(status);
    }

    public RedirectResult(String url, HttpStatus status) {
        this.url = url;
        this.status(status);
    }

    public RedirectResult with(String url) {
        this.url = url;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public int getStatus() {
        return this.getResponse().getStatus();
    }

    @Override
    public String render() {
        return "";
    }

    @Override
    public Response toResponse(
        Request request,
        Response response,
        Object result
    ) {
        return response.redirect(this.url, this.getResponse().getStatus());
    }
}
