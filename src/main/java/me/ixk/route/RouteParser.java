package me.ixk.route;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteParser {
    protected static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile(
        "\\{([^/]+)}"
    );

    protected static final String PATH_VARIABLE_REPLACE = "[^/]+";

    public RouteData parse(String route) {
        route = route.trim();
        Matcher matcher = PATH_VARIABLE_PATTERN.matcher(route);
        String routeRegex = route;
        List<String> variableNames = new ArrayList<>();
        while (matcher.find()) {
            String[] ms = matcher.group(1).split(":");
            routeRegex =
                routeRegex.replace(
                    matcher.group(0),
                    "(" +
                    (ms.length != 1 ? ms[1].trim() : PATH_VARIABLE_REPLACE) +
                    ")"
                );
            variableNames.add(ms[0].trim());
        }
        return new RouteData(route, routeRegex, variableNames);
    }
}
