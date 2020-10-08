/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Config;
import me.ixk.framework.ioc.XkJava;

@Config(name = "app")
public class AppConfig extends AbstractConfig {

    public AppConfig(final XkJava app) {
        super(app);
    }

    @Override
    public Map<String, Object> config() {
        final Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("name", this.env.get("app.name", "XK-Java"));
        map.put("version", this.env.get("app.version", "1.0"));
        map.put("locale", this.env.get("app.locale", "zh_CN"));
        map.put("env", this.env.get("app.env", "production"));
        map.put("url", this.env.get("app.url", "http://localhost"));
        map.put("port", this.env.get("app.port", "8080"));
        map.put("asset_url", this.env.get("app.asset.url", ""));
        map.put("key", this.env.get("app.key"));
        map.put("cipher", this.env.get("app.cipher", "AES/CBC/PKCS5PADDING"));
        map.put("hash.algo", this.env.get("app.hash", "bcrypt"));
        map.put("jwt.algo", this.env.get("app.jwt", "HS256"));
        map.put("jwt.default_payload", new ConcurrentHashMap<String, String>());

        return map;
    }
}
