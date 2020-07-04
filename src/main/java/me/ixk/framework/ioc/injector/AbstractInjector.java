/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import me.ixk.framework.ioc.Container;

public abstract class AbstractInjector {
    protected Container container;

    public AbstractInjector(Container container) {
        this.container = container;
    }
}
