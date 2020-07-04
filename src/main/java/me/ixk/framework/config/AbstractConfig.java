/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.Environment;

public abstract class AbstractConfig implements Config {
    protected final Application app;
    protected final Environment env;

    public AbstractConfig(Application app) {
        this.app = app;
        this.env = this.app.make(Environment.class);
    }
}
