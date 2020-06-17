package me.ixk.framework.newioc;

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
