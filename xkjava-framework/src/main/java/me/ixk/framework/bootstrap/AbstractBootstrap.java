/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import me.ixk.framework.ioc.XkJava;

public abstract class AbstractBootstrap implements Bootstrap {
    protected final XkJava app;

    public AbstractBootstrap(XkJava app) {
        this.app = app;
    }
}
