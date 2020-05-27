package me.ixk.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.ioc.Application;
import me.ixk.providers.AppProvider;

public class AppConfig extends AbstractConfig {

    public AppConfig(Application app) {
        super(app);
    }

    @Override
    public String configName() {
        return "app";
    }

    @Override
    public Map<String, Object> config() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("name", this.env.get("app.name", "XK-Java"));
        map.put("version", this.env.get("app.version", "1.0"));
        map.put("locale", this.env.get("app.locale", "zh_CN"));
        map.put("env", this.env.get("app.env", "production"));
        map.put("url", this.env.get("app.url", "http://localhost"));
        map.put("asset_url", this.env.get("app.asset.url", ""));
        map.put("key", this.env.get("app.key"));
        map.put("cipher", this.env.get("app.cipher", "AES_256_CBC"));
        map.put("hash.algo", this.env.get("app.hash", "bcrypt"));
        map.put("jwt.algo", this.env.get("app.jwt", "HS256"));

        map.put("providers", this.providers());
        return map;
    }

    private List<Class<?>> providers() {
        return Arrays.asList(AppProvider.class);
    }
}
