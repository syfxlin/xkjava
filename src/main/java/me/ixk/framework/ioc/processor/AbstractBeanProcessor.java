package me.ixk.framework.ioc.processor;

import me.ixk.framework.ioc.Container;

public abstract class AbstractBeanProcessor {
    protected Container container;

    public AbstractBeanProcessor(Container container) {
        this.container = container;
    }
}
