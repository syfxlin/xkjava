/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import me.ixk.framework.http.MimeType;

/**
 * 文本工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:11
 */
public class TextResult extends AbstractHttpResult {
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
        return MimeType.TEXT_PLAIN.asString();
    }
}
