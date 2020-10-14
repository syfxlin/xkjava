/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import org.eclipse.jetty.http.MimeTypes;

/**
 * 文本工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:11
 */
public class TextResult extends HttpResult {
    protected String text;

    public TextResult(String text) {
        this.text = text;
    }

    public void with(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String render() {
        return this.text;
    }

    @Override
    public String contentType() {
        return MimeTypes.Type.TEXT_PLAIN.asString();
    }
}
