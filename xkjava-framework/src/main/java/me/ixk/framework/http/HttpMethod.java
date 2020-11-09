/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

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
     * PATCH
     */
    PATCH,
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
