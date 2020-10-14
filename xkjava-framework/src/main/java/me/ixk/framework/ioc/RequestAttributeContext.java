/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import me.ixk.framework.helpers.Util;

/**
 * RequestAttributeContext
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 12:39
 */
public interface RequestAttributeContext extends Context {
    String BINDINGS_ATTRIBUTE_NAME = Util.attributeName(
        RequestAttributeContext.class,
        "BINDINGS_ATTRIBUTE_NAME"
    );
    String ALIAS_ATTRIBUTE_NAME = Util.attributeName(
        RequestAttributeContext.class,
        "ALIAS_ATTRIBUTE_NAME"
    );

    /**
     * 删除 Context
     */
    void removeContext();

    /**
     * 获取 Context
     *
     * @return Request 对象
     */
    HttpServletRequest getContext();

    /**
     * 设置 Context
     *
     * @param request Request 对象
     */
    void setContext(HttpServletRequest request);

    /**
     * 别名
     *
     * @return 别名 Map
     */
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

    /**
     * Bindings
     *
     * @return Binding Map
     */
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

    /**
     * 属性
     *
     * @return 属性 Map
     */
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

    /**
     * 是否启动
     *
     * @return 是否启动
     */
    @Override
    boolean isCreated();
}
