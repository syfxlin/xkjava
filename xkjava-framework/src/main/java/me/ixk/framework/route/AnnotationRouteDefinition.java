/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.lang.reflect.Method;
import java.util.Arrays;
import me.ixk.framework.annotations.RequestMethod;

/**
 * 路由注解描述
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:49
 */
public class AnnotationRouteDefinition {
    protected final String[] method;

    protected final String route;

    protected final Method handler;

    public AnnotationRouteDefinition(
        RequestMethod[] methods,
        String route,
        Method handler
    ) {
        this.method =
            Arrays
                .stream(methods)
                .map(RequestMethod::toString)
                .toArray(String[]::new);
        this.route = route;
        this.handler = handler;
    }

    public String[] getMethod() {
        return method;
    }

    public String getRoute() {
        return route;
    }

    public Method getHandler() {
        return handler;
    }
}
