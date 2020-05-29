package me.ixk.framework.route;

import java.util.List;
import java.util.Map;
import me.ixk.framework.facades.Config;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.StdErrorJson;

public class RouteManager {
    public static RouteCollector route;

    public static List<Class<?>> globalMiddleware;

    public static List<Class<?>> routeMiddleware;

    // TODO: 注解中间件

    protected RouteDispatcher dispatcher;

    @SuppressWarnings("unchecked")
    public RouteManager() {
        Map<String, List<Class<?>>> middlewareConfig = Config.get(
            "middleware",
            Map.class
        );
        Map<String, Class<? extends RouteDefinition>> routeConfig = Config.get(
            "route",
            Map.class
        );
        globalMiddleware = middlewareConfig.get("global");
        routeMiddleware = middlewareConfig.get("route");
        dispatcher =
            RouteDispatcher.dispatcher(
                routeCollector -> {
                    route = routeCollector;
                    for (Class<? extends RouteDefinition> _class : routeConfig.values()) {
                        try {
                            _class
                                .getConstructor()
                                .newInstance()
                                .routes(routeCollector);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // TODO: 注解路由
                }
            );
    }

    public Response dispatch(Request request, Response response) {
        return this.handleRequest(this.dispatcher, request, response);
    }

    public Response handleRequest(
        RouteDispatcher dispatcher,
        Request request,
        Response response
    ) {
        RouteResult routeResult = dispatcher.dispatch(
            request.getMethod(),
            request.getUri().getPath()
        );

        // 修改 Request 中的 Path 参数
        Map<String, String> pathParams;
        if ((pathParams = routeResult.getParams()) != null) {
            for (Map.Entry<String, String> entry : pathParams.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        try {
            switch (routeResult.getStatus()) {
                case NOT_FOUND:
                    response.json(
                        new StdErrorJson(
                            404,
                            "Not Found",
                            "The URI \"" +
                            request.getUri() +
                            "\" was not found."
                        )
                    );
                    break;
                case METHOD_NOT_ALLOWED:
                    response.json(
                        new StdErrorJson(
                            405,
                            "Method Not Allowed",
                            "Method \"" +
                            request.getMethod() +
                            "\" is not allowed."
                        )
                    );
                case FOUND:
                    routeResult.getHandler().handle(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
