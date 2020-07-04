/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import java.util.Map;
import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.processor.ConfigAnnotationProcessor;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.Config;

@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class LoadConfiguration extends AbstractBootstrap {

    public LoadConfiguration(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        ConfigAnnotationProcessor processor = new ConfigAnnotationProcessor(
            app
        );
        Map<String, Map<String, Object>> config = processor.processAnnotationConfig();
        this.app.instance(Config.class, new Config(this.app, config), "config");
    }
}
