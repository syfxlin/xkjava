/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Otstar Lin
 * @date 2020/10/22 下午 9:22
 */
public enum HttpMethod {
    /**
     * GET
     */
    GET,
    /**
     * POST
     */
    POST,
    /**
     * HEAD
     */
    HEAD,
    /**
     * PUT
     */
    PUT,
    /**
     * OPTIONS
     */
    OPTIONS,
    /**
     * DELETE
     */
    DELETE,
    /**
     * TRACE
     */
    TRACE,
    /**
     * CONNECT
     */
    CONNECT,
    /**
     * MOVE
     */
    MOVE,
    /**
     * PROXY
     */
    PROXY,
    /**
     * PRI
     */
    PRI,;

    public static final Map<String, HttpMethod> CACHE = new HashMap<>();

    static {
        for (final HttpMethod method : HttpMethod.values()) {
            CACHE.put(method.toString(), method);
        }
    }

    public static HttpMethod bytesToMethod(
        final byte[] bytes,
        final int position,
        final int limit
    ) {
        final int length = limit - position;
        if (length < 4) {
            return null;
        }
        switch (bytes[position]) {
            case 'G':
                if (
                    bytes[position + 1] == 'E' &&
                    bytes[position + 2] == 'T' &&
                    bytes[position + 3] == ' '
                ) {
                    return GET;
                }
                break;
            case 'P':
                if (
                    bytes[position + 1] == 'O' &&
                    bytes[position + 2] == 'S' &&
                    bytes[position + 3] == 'T' &&
                    length >= 5 &&
                    bytes[position + 4] == ' '
                ) {
                    return POST;
                }
                if (
                    bytes[position + 1] == 'R' &&
                    bytes[position + 2] == 'O' &&
                    bytes[position + 3] == 'X' &&
                    length >= 6 &&
                    bytes[position + 4] == 'Y' &&
                    bytes[position + 5] == ' '
                ) {
                    return PROXY;
                }
                if (
                    bytes[position + 1] == 'U' &&
                    bytes[position + 2] == 'T' &&
                    bytes[position + 3] == ' '
                ) {
                    return PUT;
                }
                if (
                    bytes[position + 1] == 'R' &&
                    bytes[position + 2] == 'I' &&
                    bytes[position + 3] == ' '
                ) {
                    return PRI;
                }
                break;
            case 'H':
                if (
                    bytes[position + 1] == 'E' &&
                    bytes[position + 2] == 'A' &&
                    bytes[position + 3] == 'D' &&
                    length >= 5 &&
                    bytes[position + 4] == ' '
                ) {
                    return HEAD;
                }
                break;
            case 'O':
                if (
                    bytes[position + 1] == 'P' &&
                    bytes[position + 2] == 'T' &&
                    bytes[position + 3] == 'I' &&
                    length >= 8 &&
                    bytes[position + 4] == 'O' &&
                    bytes[position + 5] == 'N' &&
                    bytes[position + 6] == 'S' &&
                    bytes[position + 7] == ' '
                ) {
                    return OPTIONS;
                }
                break;
            case 'D':
                if (
                    bytes[position + 1] == 'E' &&
                    bytes[position + 2] == 'L' &&
                    bytes[position + 3] == 'E' &&
                    length >= 7 &&
                    bytes[position + 4] == 'T' &&
                    bytes[position + 5] == 'E' &&
                    bytes[position + 6] == ' '
                ) {
                    return DELETE;
                }
                break;
            case 'T':
                if (
                    bytes[position + 1] == 'R' &&
                    bytes[position + 2] == 'A' &&
                    bytes[position + 3] == 'C' &&
                    length >= 6 &&
                    bytes[position + 4] == 'E' &&
                    bytes[position + 5] == ' '
                ) {
                    return TRACE;
                }
                break;
            case 'C':
                if (
                    bytes[position + 1] == 'O' &&
                    bytes[position + 2] == 'N' &&
                    bytes[position + 3] == 'N' &&
                    length >= 8 &&
                    bytes[position + 4] == 'E' &&
                    bytes[position + 5] == 'C' &&
                    bytes[position + 6] == 'T' &&
                    bytes[position + 7] == ' '
                ) {
                    return CONNECT;
                }
                break;
            case 'M':
                if (
                    bytes[position + 1] == 'O' &&
                    bytes[position + 2] == 'V' &&
                    bytes[position + 3] == 'E' &&
                    length >= 5 &&
                    bytes[position + 4] == ' '
                ) {
                    return MOVE;
                }
                break;
            default:
                break;
        }
        return null;
    }

    public static HttpMethod bytesToMethod(final ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return bytesToMethod(
                buffer.array(),
                buffer.arrayOffset() + buffer.position(),
                buffer.arrayOffset() + buffer.limit()
            );
        }
        return null;
    }

    public static HttpMethod from(final String method) {
        return CACHE.get(method);
    }

    public boolean is(final String s) {
        return toString().equalsIgnoreCase(s);
    }

    public String asString() {
        return toString();
    }
}
