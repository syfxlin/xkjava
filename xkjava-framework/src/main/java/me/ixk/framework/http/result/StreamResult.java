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
    private boolean async;

    public StreamResult(final InputStream stream, final MimeType contentType) {
        this(stream, contentType.asString());
    }

    public StreamResult(final InputStream stream) {
        this(stream, (String) null);
    }

    public StreamResult(final InputStream stream, final String contentType) {
        this(stream, contentType, true);
    }

    public StreamResult(
        final InputStream stream,
        final String contentType,
        final boolean async
    ) {
        this.contentType = contentType;
        this.stream = stream;
        this.async = async;
    }

    public boolean async() {
        return async;
    }

    public void async(final boolean async) {
        this.async = async;
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

    public StreamResult stream(final InputStream stream) {
        this.stream = stream;
        return this;
    }

    public InputStream stream() {
        return stream;
    }
}
