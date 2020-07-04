/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

public enum ContextName {
    APPLICATION("application"),
    REQUEST("request"),
    CONTAINER("container"),;

    final String name;

    ContextName(String contextName) {
        this.name = contextName;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
