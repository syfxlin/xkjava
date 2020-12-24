/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import java.io.IOException;
import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.exceptions.LoadEnvironmentFileException;
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
@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class LoadEnvironmentVariables extends AbstractBootstrap {

    private static final Logger log = LoggerFactory.getLogger(
        LoadEnvironmentVariables.class
    );

    public LoadEnvironmentVariables(final XkJava app) {
        super(app);
    }

    @Override
    public void boot() {
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
        environment.setPropertySource(
            this.loadProperties(location, Environment.DEFAULT_CONFIG_NAME)
        );
        // 次要配置文件
        for (String profile : environment.getActiveProfiles()) {
            environment.setPropertySource(
                this.loadProperties(location, profile)
            );
        }
        this.app.instance("env", environment);
    }

    private PropertiesPropertySource loadProperties(
        final String location,
        final String active
    ) {
        try {
            return new PropertiesPropertySource(
                active.isEmpty() ? "default" : active,
                location +
                "/" +
                String.format(
                    "application%s.properties",
                    active.isEmpty() ? "" : "-" + active
                )
            );
        } catch (final IOException e) {
            log.error("Load environment [application.properties] failed");
            throw new LoadEnvironmentFileException(
                "Load environment [application.properties] failed",
                e
            );
        }
    }
}
