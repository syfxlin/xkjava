package me.ixk.framework.route;

import me.ixk.framework.middleware.Handler;

import java.util.Map;

public class RouteResult {
    protected RouteStatus status = RouteStatus.NOT_FOUND;

    protected String route = null;

    protected Handler handler = null;

    protected Map<String, String> params = null;

    public RouteResult() {}

    public RouteResult(RouteStatus status) {
        this.status = status;
    }

    public RouteResult(
        RouteStatus status,
        Handler handler,
        String route
    ) {
        this.status = status;
        this.handler = handler;
        this.route = route;
    }

    public RouteResult(
        RouteStatus status,
        Handler handler,
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

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
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
