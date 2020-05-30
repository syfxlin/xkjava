package me.ixk.app.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.app.route.WebRoute;
import me.ixk.framework.config.AbstractConfig;
import me.ixk.framework.ioc.Application;

public class RouteConfig extends AbstractConfig {

    public RouteConfig(Application app) {
        super(app);
    }

    @Override
    public Map<String, Object> config() {
        Map<String, Object> map = new ConcurrentHashMap<>();

        map.put("web", WebRoute.class);

        return map;
    }

    @Override
    public String configName() {
        return "route";
    }
}
