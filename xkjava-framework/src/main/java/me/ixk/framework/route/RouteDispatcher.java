/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteDispatcher {
    protected final Map<String, Map<String, RouteHandler>> staticRoutes;

    protected final Map<String, MergeRouteData> variableRoutes;

    public RouteDispatcher(RouteCollector routeCollector) {
        this.staticRoutes = routeCollector.getStaticRoutes();
        this.variableRoutes = routeCollector.getVariableRoutes();
    }

    public RouteResult dispatch(String httpMethod, String url) {
        boolean methodInStatic = this.staticRoutes.containsKey(httpMethod);
        boolean methodInVariable = this.variableRoutes.containsKey(httpMethod);
        if (
            methodInStatic && this.staticRoutes.get(httpMethod).containsKey(url)
        ) {
            return new RouteResult(
                RouteStatus.FOUND,
                this.staticRoutes.get(httpMethod).get(url),
                url
            );
        }
        if (methodInVariable) {
            MergeRouteData mergeRouteData = variableRoutes.get(httpMethod);
            RouteResult result =
                this.dispatchVariableRoute(mergeRouteData, url);
            if (result.getStatus() == RouteStatus.FOUND) {
                return result;
            }
        }

        if ("HEAD".equals(httpMethod)) {
            return this.dispatch("GET", url);
        }

        for (Map<String, RouteHandler> routeData : this.staticRoutes.values()) {
            if (routeData.containsKey(url)) {
                return new RouteResult(RouteStatus.METHOD_NOT_ALLOWED);
            }
        }

        for (MergeRouteData routeData : this.variableRoutes.values()) {
            if (
                this.dispatchVariableRoute(routeData, url).getStatus() ==
                RouteStatus.FOUND
            ) {
                return new RouteResult(RouteStatus.METHOD_NOT_ALLOWED);
            }
        }

        return new RouteResult();
    }

    protected RouteResult dispatchVariableRoute(
        MergeRouteData mergeRouteData,
        String url
    ) {
        Pattern pattern = Pattern.compile(mergeRouteData.getRegex());
        Matcher matcher = pattern.matcher(url);
        if (!matcher.find()) {
            return new RouteResult();
        }
        int index;
        //noinspection StatementWithEmptyBody
        for (index = 1; matcher.group(index) == null; index++) {}
        RouteData routeData = mergeRouteData.getRouteMap().get(index - 1);
        Map<String, String> routeParams = new ConcurrentHashMap<>();
        index++;
        for (String paramName : routeData.getVariableNames()) {
            routeParams.put(paramName, matcher.group(index++));
        }
        return new RouteResult(
            RouteStatus.FOUND,
            routeData.getHandler(),
            routeParams,
            routeData.getRoute()
        );
    }
}
