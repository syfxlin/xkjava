package me.ixk.framework.route;

import java.util.Arrays;
import me.ixk.framework.annotations.RequestMethod;
import me.ixk.framework.middleware.Handler;

public class AnnotationRouteDefinition {
    protected String[] method;

    protected String route;

    protected Handler handler;

    public AnnotationRouteDefinition(
        RequestMethod[] methods,
        String route,
        Handler handler
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

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
