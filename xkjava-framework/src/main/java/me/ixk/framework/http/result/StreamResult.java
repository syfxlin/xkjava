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

    public StreamResult(MimeType contentType, InputStream stream) {
        this(contentType.asString(), stream);
    }

    public StreamResult(String contentType, InputStream stream) {
        this.contentType = contentType;
        this.stream = stream;
    }

    @Override
    public String contentType() {
        return this.contentType;
    }

    @Override
    public Response toResponse(
        Request request,
        Response response,
        Object result
    ) {
        response.contentType(this.contentType).content(this.stream);
        return response;
    }

    public StreamResult contentType(MimeType contentType) {
        return this.contentType(contentType.asString());
    }

    public StreamResult contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public StreamResult with(InputStream stream) {
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
