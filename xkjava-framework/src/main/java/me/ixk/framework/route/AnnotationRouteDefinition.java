/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.lang.reflect.Method;
import java.util.Arrays;
import me.ixk.framework.http.HttpMethod;
import me.ixk.framework.servlet.HandlerMethod;

/**
 * 路由注解描述
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:49
 */
public class AnnotationRouteDefinition {

    protected final String[] method;

    protected final String route;

    protected final HandlerMethod handler;

    public AnnotationRouteDefinition(
        final HttpMethod[] methods,
        final String route,
        final Method handler
    ) {
        this.method =
            Arrays
                .stream(methods)
                .map(HttpMethod::asString)
                .toArray(String[]::new);
        this.route = route;
        this.handler = new HandlerMethod(handler);
    }

    public String[] getMethod() {
        return method;
    }

    public String getRoute() {
        return route;
    }

    public HandlerMethod getHandler() {
        return handler;
    }
}
