/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import me.ixk.framework.ioc.Application;

public abstract class AbstractBootstrap implements Bootstrap {
    protected final Application app;

    public AbstractBootstrap(Application app) {
        this.app = app;
    }
}
