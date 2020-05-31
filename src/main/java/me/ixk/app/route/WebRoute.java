package me.ixk.app.route;

import me.ixk.app.middleware.Handler1;
import me.ixk.framework.route.RouteCollector;
import me.ixk.framework.route.RouteDefinition;

public class WebRoute implements RouteDefinition {

    @Override
    public void routes(RouteCollector r) {
        r
            .middleware("middleware1")
            .group(
                "/user",
                rr -> {
                    rr.middleware("middleware2").get("", new Handler1());
                    rr.get("/{id: \\d+}", new Handler1());
                    rr.get("/{id: \\d+}/{name}", new Handler1());
                }
            );
    }
}
