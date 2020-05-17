package me.ixk;

import me.ixk.middleware.Handler1;
import me.ixk.route.DispatcherResult;
import me.ixk.route.RouteDispatcher;

public class App {

    public static void main(String[] args) {
        RouteDispatcher dispatcher = RouteDispatcher.dispatcher(
            r -> {
                r.addRoute("GET", "/user", new Handler1());
                r.addRoute("GET", "/user/{id: \\d+}", new Handler1());
                r.addRoute("GET", "/user/{id: \\d+}/{name}", new Handler1());
            }
        );
        DispatcherResult result = dispatcher.dispatch(
            "POST",
            "/user/1/syfxlin"
        );
    }
}
