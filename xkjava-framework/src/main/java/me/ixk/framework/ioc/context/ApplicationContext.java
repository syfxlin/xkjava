/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import java.util.Map;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Context;
import me.ixk.framework.ioc.ContextItem;

public class ApplicationContext implements Context {
    private final ContextItem contextItem = new ContextItem();

    @Override
    public String getName() {
        return ContextName.APPLICATION.getName();
    }

    @Override
    public Map<String, String> getAliases() {
        return this.contextItem.getAliases();
    }

    @Override
    public Map<String, Binding> getBindings() {
        return this.contextItem.getBindings();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.contextItem.getAttributes();
    }

    @Override
    public boolean matchesScope(ScopeType scopeType) {
        switch (scopeType) {
            case SINGLETON:
            case PROTOTYPE:
                return true;
            default:
                return false;
        }
    }
}
