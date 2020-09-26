/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

public enum ScopeType {
    SINGLETON,
    PROTOTYPE,
    REQUEST,;

    public boolean isShared() {
        return this != PROTOTYPE;
    }

    public boolean isRequest() {
        return this == REQUEST;
    }

    public boolean isSingleton() {
        return this == SINGLETON;
    }

    public boolean isPrototype() {
        return this == PROTOTYPE;
    }
}
