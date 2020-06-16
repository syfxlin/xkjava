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
}
