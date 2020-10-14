/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Map;

/**
 * ThreadLocalContext
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 12:42
 */
public interface ThreadLocalContext extends Context {
    /**
     * 获取 Context
     *
     * @return ContextItem 对象
     */
    ContextItem getContext();

    /**
     * 设置 Context
     *
     * @param contextItem ContextItem 对象
     */
    void setContext(ContextItem contextItem);

    /**
     * 删除 Context
     */
    void removeContext();

    /**
     * 创建 Context
     */
    default void createContext() {
        this.setContext(new ContextItem());
    }

    /**
     * 别名
     *
     * @return 别名 Map
     */
    @Override
    default Map<String, String> getAliases() {
        return this.getContext().getAliases();
    }

    /**
     * Bindings
     *
     * @return Binding Map
     */
    @Override
    default Map<String, Binding> getBindings() {
        return this.getContext().getBindings();
    }

    /**
     * 属性
     *
     * @return 属性 Map
     */
    @Override
    default Map<String, Object> getAttributes() {
        return this.getContext().getAttributes();
    }

    /**
     * 是否启动
     *
     * @return 是否启动
     */
    @Override
    boolean isCreated();
}
