/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

public enum ScopeType {
    SINGLETON("singleton"),
    PROTOTYPE("prototype"),
    REQUEST("request"),;

    private final String name;

    ScopeType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

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
