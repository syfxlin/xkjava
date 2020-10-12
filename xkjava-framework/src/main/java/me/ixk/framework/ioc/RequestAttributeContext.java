/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import me.ixk.framework.helpers.Util;

public interface RequestAttributeContext extends Context {
    String BINDINGS_ATTRIBUTE_NAME = Util.attributeName(
        RequestAttributeContext.class,
        "BINDINGS_ATTRIBUTE_NAME"
    );
    String ALIAS_ATTRIBUTE_NAME = Util.attributeName(
        RequestAttributeContext.class,
        "ALIAS_ATTRIBUTE_NAME"
    );

    void removeContext();

    HttpServletRequest getContext();

    void setContext(HttpServletRequest request);

    @Override
    @SuppressWarnings("unchecked")
    default Map<String, String> getAliases() {
        Map<String, String> alias = (Map<String, String>) this.getContext()
            .getAttribute(ALIAS_ATTRIBUTE_NAME);
        if (alias == null) {
            alias = new ConcurrentHashMap<>();
            this.getContext().setAttribute(ALIAS_ATTRIBUTE_NAME, alias);
        }
        return alias;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Map<String, Binding> getBindings() {
        Map<String, Binding> bindings = (Map<String, Binding>) this.getContext()
            .getAttribute(BINDINGS_ATTRIBUTE_NAME);
        if (bindings == null) {
            bindings = new ConcurrentHashMap<>();
            this.getContext().setAttribute(BINDINGS_ATTRIBUTE_NAME, bindings);
        }
        return bindings;
    }

    @Override
    default Map<String, Object> getAttributes() {
        Enumeration<String> names = this.getContext().getAttributeNames();
        Map<String, Object> attributes = new ConcurrentHashMap<>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            attributes.put(name, this.getContext().getAttribute(name));
        }
        return attributes;
    }

    @Override
    boolean isCreated();
}
