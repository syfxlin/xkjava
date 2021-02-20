/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.exception;

import java.util.Collections;
import java.util.Map;
import me.ixk.framework.http.HttpStatus;

/**
 * HTTP 状态异常
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:05
 */
public class HttpException extends Exception {
    private final int code;
    private final Map<String, String> headers;

    public HttpException() {
        this(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpException(HttpStatus status) {
        this(status, status.getReasonPhrase());
    }

    public HttpException(HttpStatus status, String message) {
        this(status, message, Collections.emptyMap());
    }

    public HttpException(HttpStatus status, String message, Throwable cause) {
        super(message == null ? status.getReasonPhrase() : message, cause);
        this.code = status.getValue();
        this.headers = Collections.emptyMap();
    }

    public HttpException(
        HttpStatus status,
        String message,
        Map<String, String> headers
    ) {
        super(message == null ? status.getReasonPhrase() : message);
        this.code = status.getValue();
        this.headers = headers;
    }

    public HttpException(
        HttpStatus status,
        String message,
        Map<String, String> headers,
        Throwable cause
    ) {
        super(message == null ? status.getReasonPhrase() : message, cause);
        this.code = status.getValue();
        this.headers = headers;
    }

    protected HttpException(
        HttpStatus status,
        String message,
        Map<String, String> headers,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(
            message == null ? status.getReasonPhrase() : message,
            cause,
            enableSuppression,
            writableStackTrace
        );
        this.code = status.getValue();
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getReason() {
        return this.getMessage();
    }

    public int getCode() {
        return code;
    }
}
