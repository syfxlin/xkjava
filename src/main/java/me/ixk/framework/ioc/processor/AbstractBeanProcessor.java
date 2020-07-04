/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import me.ixk.framework.ioc.Container;

public abstract class AbstractBeanProcessor {
    protected Container container;

    public AbstractBeanProcessor(Container container) {
        this.container = container;
    }
}
