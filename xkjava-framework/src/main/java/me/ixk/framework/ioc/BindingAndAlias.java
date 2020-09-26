/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BindingAndAlias {
    private final Map<String, Binding> bindings = new ConcurrentHashMap<>(256);

    private final Map<String, String> aliases = new ConcurrentHashMap<>(256);

    public Map<String, Binding> getBindings() {
        return bindings;
    }

    public Map<String, String> getAliases() {
        return aliases;
    }
}
