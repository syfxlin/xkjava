/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import java.io.InputStream;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

/**
 * Stream 响应
 *
 * @author Otstar Lin
 * @date 2020/12/20 下午 7:01
 */
public class StreamResult extends AbstractHttpResult {

    private String contentType;
    private InputStream stream;

    public StreamResult(final InputStream stream, final MimeType contentType) {
        this(contentType.asString(), stream);
    }

    public StreamResult(final String contentType, final InputStream stream) {
        this.contentType = contentType;
        this.stream = stream;
    }

    public StreamResult(final InputStream stream) {
        this.contentType = null;
        this.stream = stream;
    }

    @Override
    public String contentType() {
        return this.contentType;
    }

    @Override
    public boolean toResponse(
        final Request request,
        final Response response,
        final Object result
    ) {
        if (this.contentType != null) {
            response.contentType(this.contentType);
        }
        response.content(this.stream);
        return true;
    }

    public StreamResult contentType(final MimeType contentType) {
        return this.contentType(contentType.asString());
    }

    public StreamResult contentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    public StreamResult with(final InputStream stream) {
        this.stream = stream;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getStream() {
        return stream;
    }
}
