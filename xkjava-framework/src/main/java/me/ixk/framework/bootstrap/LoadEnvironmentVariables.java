/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import java.io.IOException;
import java.util.Properties;
import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.exceptions.LoadEnvironmentFileException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.Environment;
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

    public LoadEnvironmentVariables(XkJava app) {
        super(app);
    }

    @Override
    public void boot() {
        Properties property = new Properties();
        try {
            property.load(
                this.getClass().getResourceAsStream("/application.properties")
            );
        } catch (IOException e) {
            log.error("Load environment [application.properties] failed");
            throw new LoadEnvironmentFileException(
                "Load environment [application.properties] failed",
                e
            );
        }
        this.app.instance(
                Environment.class,
                new Environment(this.app, property),
                "env"
            );
    }
}
