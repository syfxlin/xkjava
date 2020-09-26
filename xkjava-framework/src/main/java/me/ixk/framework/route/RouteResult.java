/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RouteResult {
    protected RouteStatus status = RouteStatus.NOT_FOUND;

    protected String route = null;

    protected RouteHandler handler = null;

    protected Map<String, String> params = null;

    public RouteResult() {}

    public RouteResult(RouteStatus status) {
        this.status = status;
    }

    public RouteResult(RouteStatus status, RouteHandler handler, String route) {
        this(status, handler, new ConcurrentHashMap<>(), route);
    }

    public RouteResult(
        RouteStatus status,
        RouteHandler handler,
        Map<String, String> params,
        String route
    ) {
        this.status = status;
        this.handler = handler;
        this.params = params;
        this.route = route;
    }

    public RouteStatus getStatus() {
        return status;
    }

    public void setStatus(RouteStatus status) {
        this.status = status;
    }

    public RouteHandler getHandler() {
        return handler;
    }

    public void setHandler(RouteHandler handler) {
        this.handler = handler;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}