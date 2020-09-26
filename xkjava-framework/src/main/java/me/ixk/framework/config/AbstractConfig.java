/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.Environment;

public abstract class AbstractConfig implements Config {
    protected final XkJava app;
    protected final Environment env;

    public AbstractConfig(XkJava app) {
        this.app = app;
        this.env = this.app.make(Environment.class);
    }
}
