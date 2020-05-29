package me.ixk.framework.route;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RouteGenerator {

    public MergeRouteData mergeVariableRoutes(List<RouteData> variableRoutes) {
        if (variableRoutes.isEmpty()) {
            return null;
        }
        Map<Integer, RouteData> routeMap = new ConcurrentHashMap<>();
        int numGroups = 0;
        StringBuilder regex = new StringBuilder();
        regex.append("^(?:");
        for (RouteData routeData : variableRoutes) {
            int variableSize = routeData.getVariableSize();
            regex.append("(").append(routeData.getRegex()).append(")|");
            routeMap.put(numGroups, routeData);
            numGroups += variableSize + 1;
        }
        regex.deleteCharAt(regex.length() - 1);
        regex.append(")$");
        return new MergeRouteData(regex.toString(), routeMap);
    }
}
