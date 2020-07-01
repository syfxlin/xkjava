package me.ixk.framework.ioc.injector;

import me.ixk.framework.ioc.Container;

public abstract class AbstractInjector {
    protected Container container;

    public AbstractInjector(Container container) {
        this.container = container;
    }
}
