/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Component;

@Component
public class RouteGenerator {

    public MergeRouteData mergeVariableRoutes(
        final List<RouteData> variableRoutes
    ) {
        if (variableRoutes.isEmpty()) {
            return null;
        }
        final Map<Integer, RouteData> routeMap = new ConcurrentHashMap<>();
        int numGroups = 0;
        final StringBuilder regex = new StringBuilder();
        regex.append("^(?:");
        for (final RouteData routeData : variableRoutes) {
            final int variableSize = routeData.getVariableSize();
            regex.append("(").append(routeData.getRegex()).append(")|");
            routeMap.put(numGroups, routeData);
            numGroups += variableSize + 1;
        }
        regex.deleteCharAt(regex.length() - 1);
        regex.append(")$");
        return new MergeRouteData(regex.toString(), routeMap);
    }
}
