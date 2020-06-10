package me.ixk.framework.exceptions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.http.ResponseReason;

public class HttpException extends Exception {
    private int status;
    private String reason;
    private Map<String, String> headers;

    public HttpException() {
        this(500);
    }

    public HttpException(int status) {
        this(status, ResponseReason.getMessage(status));
    }

    public HttpException(int status, String message) {
        this(status, message, null, new ConcurrentHashMap<>());
    }

    public HttpException(int status, String message, String reason) {
        this(status, message, null, new ConcurrentHashMap<>());
    }

    public HttpException(int status, String message, Throwable cause) {
        super(message, cause);
        this.setStatus(status);
        this.setHeaders(new ConcurrentHashMap<>());
    }

    public HttpException(
        int status,
        String message,
        String reason,
        Map<String, String> headers
    ) {
        super(message);
        this.setStatus(status);
        this.setReason(reason);
        this.setHeaders(headers);
    }

    public HttpException(
        int status,
        String message,
        String reason,
        Map<String, String> headers,
        Throwable cause
    ) {
        super(message, cause);
        this.setStatus(status);
        this.setReason(reason);
        this.setHeaders(headers);
    }

    protected HttpException(
        int status,
        String message,
        String reason,
        Map<String, String> headers,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.setStatus(status);
        this.setReason(reason);
        this.setHeaders(headers);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        if (reason == null) {
            this.reason = ResponseReason.getMessage(this.getStatus());
        } else {
            this.reason = reason;
        }
    }
}
