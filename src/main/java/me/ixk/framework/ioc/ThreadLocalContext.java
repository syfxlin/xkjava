/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Map;
import me.ixk.framework.annotations.ScopeType;

public interface ThreadLocalContext extends Context {
    BindingAndAlias getContext();
    void setContext(BindingAndAlias bindingAndAlias);
    void removeContext();
    boolean isCreated();

    default void createContext() {
        this.setContext(new BindingAndAlias());
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
    default void setAttribute(String name, Object attribute) {
        this.setBinding(
                ATTRIBUTE_PREFIX + name,
                new Binding(attribute, ScopeType.REQUEST)
            );
    }
}
