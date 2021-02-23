/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import me.ixk.framework.http.MimeType;

/**
 * HTML 响应
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:10
 */
public class HtmlResult extends AbstractHttpResult {

    protected String html;

    public HtmlResult(final String html) {
        this.html = html;
    }

    public HtmlResult html(final String html) {
        this.html = html;
        return this;
    }

    public String html() {
        return html;
    }

    @Override
    public String render() {
        return this.html;
    }

    @Override
    public String contentType() {
        return MimeType.TEXT_HTML.asString();
    }
}
