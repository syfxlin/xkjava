/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import cn.hutool.core.io.IoUtil;
import java.io.IOException;
import java.util.Properties;
import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.exceptions.LoadEnvironmentFileException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.utils.ResourceUtils;
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

    private static final String CONFIG_LOCATION_NAME = "xkjava.config.location";
    private static final String CONFIG_ACTIVE_NAME = "xkjava.config.active";
    private static final String DEFAULT_CONFIG_LOCATION =
        "classpath:/application.properties";

    public LoadEnvironmentVariables(final XkJava app) {
        super(app);
    }

    @Override
    public void boot() {
        final Properties properties = new Properties();
        // 先读取传入的参数
        final Properties cliProps = this.parseCliProperties();
        // 如果配置了路径，则使用新的路径
        final String configLocation = (String) cliProps.getOrDefault(
            CONFIG_LOCATION_NAME,
            DEFAULT_CONFIG_LOCATION
        );
        // 使用获得的路径读取主要的配置文件
        final Properties primaryProps =
            this.parseFileProperties(configLocation);
        // 添加进配置中
        properties.putAll(primaryProps);
        // 次要配置文件
        String activeName = (String) cliProps.get(CONFIG_ACTIVE_NAME);
        if (activeName == null) {
            activeName = (String) primaryProps.get(CONFIG_ACTIVE_NAME);
        }
        if (activeName != null) {
            final Properties secondaryProps =
                this.parseFileProperties(
                        configLocation.replace(
                            ".properties",
                            String.format("-%s.properties", activeName)
                        )
                    );
            properties.putAll(secondaryProps);
        }
        // 最终把 Cli 的配置覆盖到所有配置中
        properties.putAll(cliProps);
        this.app.instance(
                Environment.class,
                new Environment(this.app, properties),
                "env"
            );
    }

    private Properties parseCliProperties() {
        final Properties properties = new Properties();
        for (final String arg : this.app.args()) {
            if (!arg.startsWith("--") || !arg.contains("=")) {
                throw new LoadEnvironmentFileException(
                    "Incorrect command parameter format"
                );
            }
            final String[] kv = arg.substring(2).split("=");
            properties.put(kv[0], kv[1]);
        }
        return properties;
    }

    private Properties parseFileProperties(final String filePath) {
        final Properties property = new Properties();
        try {
            property.load(IoUtil.toStream(ResourceUtils.getFile(filePath)));
        } catch (final IOException e) {
            log.error("Load environment [application.properties] failed");
            throw new LoadEnvironmentFileException(
                "Load environment [application.properties] failed",
                e
            );
        }
        return property;
    }
}
