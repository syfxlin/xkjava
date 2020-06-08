package me.ixk.framework.route;

import java.util.Arrays;
import me.ixk.framework.annotations.RequestMethod;

public class AnnotationRouteDefinition {
    protected String[] method;

    protected String route;

    protected String handler;

    public AnnotationRouteDefinition(
        RequestMethod[] methods,
        String route,
        String handler
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

    public void setMethod(String[] method) {
        this.method = method;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}
