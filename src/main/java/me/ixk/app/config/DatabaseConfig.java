/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.config;

import me.ixk.framework.annotations.Config;
import me.ixk.framework.config.AbstractConfig;
import me.ixk.framework.ioc.Application;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Config(name = "database")
public class DatabaseConfig extends AbstractConfig {

    public DatabaseConfig(Application app) {
        super(app);
    }

    @Override
    public Map<String, Object> config() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("driver", this.env.get("database.driver"));
        map.put("url", this.env.get("database.url"));
        map.put("username", this.env.get("database.username"));
        map.put("password", this.env.get("database.password"));

        map.put("mapper_packages", this.mapperPackages());
        return map;
    }

    private List<String> mapperPackages() {
        return Arrays.asList("me.ixk.app.mapper");
    }
}
