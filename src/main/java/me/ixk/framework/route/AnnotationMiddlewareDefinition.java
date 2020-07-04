/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import me.ixk.framework.middleware.Middleware;

public class AnnotationMiddlewareDefinition {
    protected String value;

    protected Class<? extends Middleware>[] middleware;

    protected String handler;

    public AnnotationMiddlewareDefinition(
        String value,
        Class<? extends Middleware>[] middleware,
        String handler
    ) {
        this.value = value;
        this.middleware = middleware;
        this.handler = handler;
    }

    public boolean isClass() {
        return this.value.equals("");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Class<? extends Middleware>[] getMiddleware() {
        return middleware;
    }

    public void setMiddleware(Class<? extends Middleware>[] middleware) {
        this.middleware = middleware;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}
