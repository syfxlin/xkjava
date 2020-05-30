package me.ixk.app.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.app.middleware.Middleware1;
import me.ixk.app.middleware.Middleware2;
import me.ixk.framework.config.AbstractConfig;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.middleware.Middleware;

public class MiddlewareConfig extends AbstractConfig {

    public MiddlewareConfig(Application app) {
        super(app);
    }

    @Override
    public Map<String, Object> config() {
        List<Class<? extends Middleware>> globalMiddleware = new ArrayList<>();
        Map<String, Class<? extends Middleware>> routeMiddleware = new ConcurrentHashMap<>();

        routeMiddleware.put("middleware1", Middleware1.class);
        routeMiddleware.put("middleware2", Middleware2.class);

        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("global", globalMiddleware);
        map.put("route", routeMiddleware);
        return map;
    }

    @Override
    public String configName() {
        return "middleware";
    }
}
