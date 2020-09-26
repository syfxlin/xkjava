/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import java.util.Map;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.BindingAndAlias;
import me.ixk.framework.ioc.Context;

public class ApplicationContext implements Context {
    private final BindingAndAlias bindingAndAlias = new BindingAndAlias();

    @Override
    public String getName() {
        return ContextName.APPLICATION.getName();
    }

    @Override
    public Map<String, Binding> getBindings() {
        return this.bindingAndAlias.getBindings();
    }

    @Override
    public Map<String, String> getAliases() {
        return this.bindingAndAlias.getAliases();
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
