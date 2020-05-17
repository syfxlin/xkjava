package me.ixk.route;

import java.util.Map;
import me.ixk.middleware.HandlerInterface;

public class RouteResult {
    protected RouteStatus status = RouteStatus.NOT_FOUND;

    protected String route = null;

    protected HandlerInterface handler = null;

    protected Map<String, String> params = null;

    public RouteResult() {}

    public RouteResult(RouteStatus status) {
        this.status = status;
    }

    public RouteResult(
        RouteStatus status,
        HandlerInterface handler,
        String route
    ) {
        this.status = status;
        this.handler = handler;
        this.route = route;
    }

    public RouteResult(
        RouteStatus status,
        HandlerInterface handler,
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

    public HandlerInterface getHandler() {
        return handler;
    }

    public void setHandler(HandlerInterface handler) {
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