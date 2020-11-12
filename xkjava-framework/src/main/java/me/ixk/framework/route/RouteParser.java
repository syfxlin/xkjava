/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.ixk.framework.annotations.Component;

/**
 * 路由解析器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:51
 */
@Component(name = "routeParser")
public class RouteParser {
    protected static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile(
        "\\{([a-zA-Z$_][a-zA-Z0-9$_]*)(\\?)?(:[^/}]+)?}"
    );

    protected static final String PATH_VARIABLE_REPLACE = "[^/]+";

    public RouteData parse(String route) {
        route = route.trim();
        final Matcher matcher = PATH_VARIABLE_PATTERN.matcher(route);
        String routeRegex = route;
        final List<String> variableNames = new ArrayList<>();
        while (matcher.find()) {
            final String variableName = matcher.group(1).trim();
            final boolean isOptional = matcher.group(2) != null;
            final String customPattern = matcher.group(3);
            routeRegex =
                routeRegex.replace(
                    matcher.group(0),
                    String.format(
                        "%s(%s)%s",
                        isOptional ? "?" : "",
                        customPattern == null
                            ? PATH_VARIABLE_REPLACE
                            : customPattern.trim().substring(1),
                        isOptional ? "?" : ""
                    )
                );
            variableNames.add(variableName);
        }
        return new RouteData(route, routeRegex, variableNames);
    }
}
