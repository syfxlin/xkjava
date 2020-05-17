package me.ixk.route;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.ixk.middleware.HandlerInterface;

public class RouteDispatcher {
    protected Map<String, Map<String, HandlerInterface>> staticRoutes;

    protected Map<String, MergeRouteData> variableRoutes;

    public RouteDispatcher(RouteCollector routeCollector) {
        this.staticRoutes = routeCollector.getStaticRoutes();
        this.variableRoutes = routeCollector.getVariableRoutes();
    }

    public static RouteDispatcher dispatcher(RouteDefinition routeDefinition) {
        RouteCollector routeCollector = new RouteCollector(
            new RouteParser(),
            new RouteGenerator()
        );
        routeDefinition.invoke(routeCollector);

        return new RouteDispatcher(routeCollector);
    }

    public DispatcherResult dispatch(String httpMethod, String url) {
        boolean methodInStatic = this.staticRoutes.containsKey(httpMethod);
        boolean methodInVariable = this.variableRoutes.containsKey(httpMethod);
        if (
            methodInStatic && this.staticRoutes.get(httpMethod).containsKey(url)
        ) {
            return new DispatcherResult(
                DispatcherStatus.FOUND,
                this.staticRoutes.get(httpMethod).get(url)
            );
        }
        if (methodInVariable) {
            MergeRouteData mergeRouteData = variableRoutes.get(httpMethod);
            DispatcherResult result =
                this.dispatchVariableRoute(mergeRouteData, url);
            if (result.getStatus() == DispatcherStatus.FOUND) {
                return result;
            }
        }

        if (httpMethod.equals("HEAD")) {
            return this.dispatch("GET", url);
        }

        for (Map<String, HandlerInterface> routeData : this.staticRoutes.values()) {
            if (routeData.containsKey(url)) {
                return new DispatcherResult(
                    DispatcherStatus.METHOD_NOT_ALLOWED
                );
            }
        }

        for (MergeRouteData routeData : this.variableRoutes.values()) {
            if (
                this.dispatchVariableRoute(routeData, url).getStatus() ==
                DispatcherStatus.FOUND
            ) {
                return new DispatcherResult(
                    DispatcherStatus.METHOD_NOT_ALLOWED
                );
            }
        }

        return new DispatcherResult();
    }

    protected DispatcherResult dispatchVariableRoute(
        MergeRouteData mergeRouteData,
        String url
    ) {
        Pattern pattern = Pattern.compile(mergeRouteData.getRegex());
        Matcher matcher = pattern.matcher(url);
        if (!matcher.find()) {
            return new DispatcherResult();
        }
        int index;
        for (index = 1; matcher.group(index) == null; index++);
        RouteData routeData = mergeRouteData.getRouteMap().get(index - 1);
        Map<String, String> routeParams = new ConcurrentHashMap<>();
        index++;
        for (String paramName : routeData.getVariableNames()) {
            routeParams.put(paramName, matcher.group(index++));
        }
        return new DispatcherResult(
            DispatcherStatus.FOUND,
            routeData.getHandler(),
            routeParams
        );
    }
}
