/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.servlet.HandlerMethod;

/**
 * 路由结果
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:52
 */
public class RouteInfo {

    protected volatile RouteStatus status = RouteStatus.NOT_FOUND;

    protected volatile String route = null;

    protected HandlerMethod handler = null;

    protected Map<String, String> params = null;

    public RouteInfo() {}

    public RouteInfo(RouteStatus status) {
        this.status = status;
    }

    public RouteInfo(RouteStatus status, HandlerMethod handler, String route) {
        this(status, handler, new ConcurrentHashMap<>(), route);
    }

    public RouteInfo(
        RouteStatus status,
        HandlerMethod handler,
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

    public HandlerMethod getHandler() {
        return handler;
    }

    public void setHandler(HandlerMethod handler) {
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
