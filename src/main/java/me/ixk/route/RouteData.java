package me.ixk.route;

import java.util.List;
import me.ixk.middleware.HandlerInterface;

public class RouteData {
    protected String route;

    protected HandlerInterface handler;

    protected List<String> variableNames;

    public RouteData(String route, List<String> variableNames) {
        this.route = route;
        this.variableNames = variableNames;
    }

    public boolean matches(String route) {
        return route.matches(this.route);
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setHandler(HandlerInterface handler) {
        this.handler = handler;
    }

    public HandlerInterface getHandler() {
        return handler;
    }

    public List<String> getVariableNames() {
        return variableNames;
    }

    public void setVariableNames(List<String> variableNames) {
        this.variableNames = variableNames;
    }

    public int getVariableSize() {
        return variableNames.size();
    }
}
