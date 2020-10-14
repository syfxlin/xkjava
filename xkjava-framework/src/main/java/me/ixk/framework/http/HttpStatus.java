/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import cn.hutool.core.util.ReflectUtil;

/**
 * 响应码
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:15
 */
public enum HttpStatus {
    /**
     * Continue
     */
    CONTINUE(100, "Continue"),
    /**
     * Switching Protocols
     */
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    /**
     * Processing
     */
    PROCESSING(102, "Processing"),
    /**
     * Early Hints
     */
    EARLY_HINTS(103, "Early Hints"),
    /**
     * OK
     */
    OK(200, "OK"),
    /**
     * Created
     */
    CREATED(201, "Created"),
    /**
     * Accepted
     */
    ACCEPTED(202, "Accepted"),
    /**
     * Non-Authoritative Information
     */
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    /**
     * No Content
     */
    NO_CONTENT(204, "No Content"),
    /**
     * Reset Content
     */
    RESET_CONTENT(205, "Reset Content"),
    /**
     * Partial Content
     */
    PARTIAL_CONTENT(206, "Partial Content"),
    /**
     * Multi-Status
     */
    MULTI_STATUS(207, "Multi-Status"),
    /**
     * Already Reported
     */
    ALREADY_REPORTED(208, "Already Reported"),
    /**
     * IM Used
     */
    IM_USED(209, "IM Used"),
    /**
     * Multiple Choices
     */
    MULTIPLE_CHOICES(300, "Multiple Choices"),
    /**
     * Moved Permanently
     */
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    /**
     * Found
     */
    FOUND(302, "Found"),
    /**
     * See Other
     */
    SEE_OTHER(303, "See Other"),
    /**
     * Not Modified
     */
    NOT_MODIFIED(304, "Not Modified"),
    /**
     * Use Proxy
     */
    USE_PROXY(305, "Use Proxy"),
    /**
     * Switch Proxy
     */
    SWITCH_PROXY(306, "Switch Proxy"),
    /**
     * Temporary Redirect
     */
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    /**
     * Permanent Redirect
     */
    PERMANENT_REDIRECT(308, "Permanent Redirect"),
    /**
     * Bad Request
     */
    BAD_REQUEST(400, "Bad Request"),
    /**
     * Unauthorized
     */
    UNAUTHORIZED(401, "Unauthorized"),
    /**
     * Payment Required
     */
    PAYMENT_REQUIRED(402, "Payment Required"),
    /**
     * Forbidden
     */
    Forbidden(403, "Forbidden"),
    /**
     * Not Found
     */
    NOT_FOUND(404, "Not Found"),
    /**
     * Method Not Allowed
     */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    /**
     * Not Acceptable
     */
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    /**
     * Proxy Authentication Required
     */
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    /**
     * Request Timeout
     */
    REQUEST_TIMEOUT(408, "Request Timeout"),
    /**
     * Conflict
     */
    CONFLICT(409, "Conflict"),
    /**
     * Gone
     */
    GONE(410, "Gone"),
    /**
     * Length Required
     */
    LENGTH_REQUIRED(411, "Length Required"),
    /**
     * Precondition Failed
     */
    PRECONDITION_FAILED(412, "Precondition Failed"),
    /**
     * Payload Too Large
     */
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    /**
     * URI Too Long
     */
    URI_TOO_LONG(414, "URI Too Long"),
    /**
     * Unsupported Media Type
     */
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    /**
     * Range Not Satisfiable
     */
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
    /**
     * Expectation Failed
     */
    EXPECTATION_FAILED(417, "Expectation Failed"),
    /**
     * I'm a teapot
     */
    I_AM_A_TEAPOT(418, "I'm a teapot"),
    /**
     * Request Expired
     */
    REQUEST_EXPIRED(419, "Request Expired"),
    /**
     * Misdirected Request
     */
    MISDIRECTED_REQUEST(421, "Misdirected Request"),
    /**
     * Unprocessable Entity
     */
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    /**
     * Locked
     */
    LOCKED(423, "Locked"),
    /**
     * Failed Dependency
     */
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    /**
     * Too Early
     */
    TOO_EARLY(425, "Too Early"),
    /**
     * Upgrade Required
     */
    UPGRADE_REQUIRED(426, "Upgrade Required"),
    /**
     * Precondition Required
     */
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    /**
     * Too Many Requests
     */
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    /**
     * Request Header Fields Too Large
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    /**
     * Connection Closed Without Response
     */
    CONNECTION_CLOSED_WITHOUT_RESPONSE(
        444,
        "Connection Closed Without Response"
    ),
    /**
     * Unavailable For Legal Reasons
     */
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),
    /**
     * Client Closed Request
     */
    CLIENT_CLOSED_REQUEST(499, "Client Closed Request"),
    /**
     * Internal Server Error
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    /**
     * Not Implemented
     */
    NOT_IMPLEMENTED(501, "Not Implemented"),
    /**
     * Bad Gateway
     */
    BAD_GATEWAY(502, "Bad Gateway"),
    /**
     * Service Unavailable
     */
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    /**
     * Gateway Timeout
     */
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    /**
     * HTTP Version Not Supported
     */
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    /**
     * Variant Also Negotiates
     */
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    /**
     * Insufficient Storage
     */
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    /**
     * Loop Detected
     */
    LOOP_DETECTED(508, "Loop Detected"),
    /**
     * Not Extended
     */
    NOT_EXTENDED(510, "Not Extended"),
    /**
     * Network Authentication Required
     */
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required"),
    /**
     * Network Connect Timeout Error
     */
    NETWORK_CONNECT_TIMEOUT_ERROR(599, "Network Connect Timeout Error"),;

    private final int value;

    private final String reasonPhrase;

    HttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    @Override
    public String toString() {
        return this.value + " " + name();
    }

    public static HttpStatus valueOf(int statusCode) {
        HttpStatus status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException(
                "No matching constant for [" + statusCode + "]"
            );
        }
        return status;
    }

    public static HttpStatus resolve(int statusCode) {
        for (HttpStatus status : values()) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    public static HttpStatus valueOf(int statusCode, String reasonPhrase) {
        HttpStatus status = resolve(statusCode);
        if (status != null) {
            return status;
        }
        return ReflectUtil.newInstance(
            HttpStatus.class,
            statusCode,
            reasonPhrase
        );
    }
}
