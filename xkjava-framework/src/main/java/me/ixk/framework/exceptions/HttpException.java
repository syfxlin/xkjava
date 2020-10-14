/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.exceptions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.http.HttpStatus;

/**
 * HTTP 状态异常
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:05
 */
public class HttpException extends Exception {
    private HttpStatus status;
    private Map<String, String> headers;

    public HttpException() {
        this(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpException(HttpStatus status) {
        this(status, status.getReasonPhrase());
    }

    public HttpException(HttpStatus status, String message) {
        this(status, message, new ConcurrentHashMap<>());
    }

    public HttpException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.setStatus(status);
        this.setHeaders(new ConcurrentHashMap<>());
    }

    public HttpException(
        HttpStatus status,
        String message,
        Map<String, String> headers
    ) {
        super(message);
        this.setStatus(status);
        this.setHeaders(headers);
    }

    public HttpException(
        HttpStatus status,
        String message,
        Map<String, String> headers,
        Throwable cause
    ) {
        super(message, cause);
        this.setStatus(status);
        this.setHeaders(headers);
    }

    protected HttpException(
        HttpStatus status,
        String message,
        Map<String, String> headers,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.setStatus(status);
        this.setHeaders(headers);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getReason() {
        return this.status.getReasonPhrase();
    }
}
