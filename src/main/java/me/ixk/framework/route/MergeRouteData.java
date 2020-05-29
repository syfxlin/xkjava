package me.ixk.framework.route;

import java.util.Map;

public class MergeRouteData {
    protected String regex;

    protected Map<Integer, RouteData> routeMap;

    public MergeRouteData(String regex, Map<Integer, RouteData> routeMap) {
        this.regex = regex;
        this.routeMap = routeMap;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Map<Integer, RouteData> getRouteMap() {
        return routeMap;
    }

    public void setRouteMap(Map<Integer, RouteData> routeMap) {
        this.routeMap = routeMap;
    }
}
