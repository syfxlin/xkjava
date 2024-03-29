/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.property.CommandLinePropertySource;
import me.ixk.framework.property.Environment;
import me.ixk.framework.property.PropertiesPropertySource;
import me.ixk.framework.property.SystemPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取配置文件
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:53
 */
public class LoadEnvironmentVariables {

    private static final Logger log = LoggerFactory.getLogger(
        LoadEnvironmentVariables.class
    );

    private final XkJava app;

    public LoadEnvironmentVariables(final XkJava app) {
        this.app = app;
    }

    public void load() {
        final Environment environment = new Environment("environment");
        // 读取系统参数
        final SystemPropertySource systemPropertySource = new SystemPropertySource(
            "system"
        );
        environment.setPropertySource(systemPropertySource);
        // 读取传入的参数
        final CommandLinePropertySource commandLinePropertySource = new CommandLinePropertySource(
            "commandLine",
            this.app.args()
        );
        environment.setPropertySource(commandLinePropertySource);
        // 如果配置了路径，则使用新的路径
        final String location = environment.get(
            Environment.CONFIG_LOCATION_NAME,
            Environment.DEFAULT_CONFIG_LOCATION
        );
        final String name = environment.get(
            Environment.CONFIG_NAME_NAME,
            Environment.DEFAULT_CONFIG_NAME
        );
        environment.setPropertySource(this.loadProperties(location, name, ""));
        // 次要配置文件
        for (String profile : environment.getActiveProfiles()) {
            environment.setPropertySource(
                this.loadProperties(location, name, profile)
            );
        }
        // 导入的其他配置文件
        final String imports = environment.get(
            Environment.CONFIG_IMPORT_NAME,
            String.class
        );
        if (imports != null) {
            for (String path : imports.trim().split(",")) {
                path = path.trim();
                environment.setPropertySource(this.loadProperties(path, path));
            }
        }
        this.app.instance("env", environment);
    }

    private PropertiesPropertySource loadProperties(
        final String name,
        final String path
    ) {
        return new PropertiesPropertySource(name, path);
    }

    private PropertiesPropertySource loadProperties(
        final String location,
        final String name,
        final String active
    ) {
        return this.loadProperties(
                active.isEmpty() ? "default" : active,
                location +
                "/" +
                String.format(
                    "%s%s.properties",
                    name,
                    active.isEmpty() ? "" : "-" + active
                )
            );
    }
}
