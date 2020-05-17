package me.ixk;

import me.ixk.middleware.Handler1;
import me.ixk.route.RouteDispatcher;
import me.ixk.route.RouteResult;

public class App {

    public static void main(String[] args) {
        RouteDispatcher dispatcher = RouteDispatcher.dispatcher(
            r -> {
                r.addGroup(
                    "/user",
                    rr -> {
                        rr.addRoute("GET", "", new Handler1());
                        rr.addRoute("GET", "/{id: \\d+}", new Handler1());
                        rr.addRoute(
                            "GET",
                            "/{id: \\d+}/{name}",
                            new Handler1()
                        );
                    }
                );
            }
        );
        RouteResult result = dispatcher.dispatch("GET", "/user/a/syfxlin");
    }
}
