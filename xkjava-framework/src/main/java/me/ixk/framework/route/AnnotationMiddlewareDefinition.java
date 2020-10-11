/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.lang.reflect.Method;
import me.ixk.framework.middleware.Middleware;

public class AnnotationMiddlewareDefinition {
    protected final String[] value;

    protected final Class<? extends Middleware>[] middleware;

    protected final Method handler;

    public AnnotationMiddlewareDefinition(
        String[] value,
        Class<? extends Middleware>[] middleware,
        Method handler
    ) {
        this.value = value;
        this.middleware = middleware;
        this.handler = handler;
    }

    public boolean isClass() {
        return this.value.length == 0;
    }

    public String[] getValue() {
        return value;
    }

    public Class<? extends Middleware>[] getMiddleware() {
        return middleware;
    }

    public Method getHandler() {
        return handler;
    }
}
