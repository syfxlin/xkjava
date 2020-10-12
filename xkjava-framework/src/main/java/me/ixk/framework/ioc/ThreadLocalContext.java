/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Map;

public interface ThreadLocalContext extends Context {
    ContextItem getContext();

    void setContext(ContextItem contextItem);

    void removeContext();

    @Override
    boolean isCreated();

    default void createContext() {
        this.setContext(new ContextItem());
    }

    @Override
    default Map<String, String> getAliases() {
        return this.getContext().getAliases();
    }

    @Override
    default Map<String, Binding> getBindings() {
        return this.getContext().getBindings();
    }

    @Override
    default Map<String, Object> getAttributes() {
        return this.getContext().getAttributes();
    }
}
