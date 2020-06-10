package me.ixk.framework.route;

import java.util.List;

public class RouteData {
    protected String regex;

    protected String route;

    protected RouteHandler handler;

    protected List<String> variableNames;

    public RouteData(String route, String regex, List<String> variableNames) {
        this.route = route;
        this.regex = regex;
        this.variableNames = variableNames;
    }

    public boolean matches(String route) {
        return route.matches(this.regex);
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String route) {
        this.regex = route;
    }

    public void setHandler(RouteHandler handler) {
        this.handler = handler;
    }

    public RouteHandler getHandler() {
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
