/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

/**
 * 请求方法
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:44
 */
public enum RequestMethod {
    /**
     * GET 请求
     */
    GET("GET"),
    /**
     * HEAD 请求
     */
    HEAD("HEAD"),
    /**
     * POST 请求
     */
    POST("POST"),
    /**
     * PUT 请求
     */
    PUT("PUT"),
    /**
     * PATCH 请求
     */
    PATCH("PATCH"),
    /**
     * DELETE 请求
     */
    DELETE("DELETE"),
    /**
     * OPTIONS 请求
     */
    OPTIONS("OPTIONS"),
    /**
     * TRACE 请求
     */
    TRACE("TRACE"),;

    private final String method;

    RequestMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return method;
    }
}
