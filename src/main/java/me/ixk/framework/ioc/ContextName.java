package me.ixk.framework.ioc;

public enum ContextName {
    APPLICATION("application"),
    REQUEST("request"),;

    final String contextName;

    ContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getName() {
        return this.contextName;
    }
}
