package me.ixk.app.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Configuration;
import me.ixk.framework.config.AbstractConfig;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.middleware.*;

@Configuration(name = "middleware")
public class MiddlewareConfig extends AbstractConfig {

    public MiddlewareConfig(Application app) {
        super(app);
    }

    @Override
    public Map<String, Object> config() {
        Map<String, Class<? extends Middleware>> routeMiddleware = new ConcurrentHashMap<>();

        routeMiddleware.put("auth", Authenticate.class);
        routeMiddleware.put("guest", RedirectIfAuthenticated.class);

        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("global", this.globalMiddleware());
        map.put("route", routeMiddleware);
        return map;
    }

    public List<Class<? extends Middleware>> globalMiddleware() {
        return Arrays.asList(EncryptCookies.class, AddQueuedCookies.class);
    }
}
