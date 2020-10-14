/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

/**
 * 内置 Context 的名称枚举
 */
public enum ContextName {
    /**
     * 应用
     */
    APPLICATION("application"),
    /**
     * 请求
     */
    REQUEST("request"),
    /**
     * 容器
     */
    CONTAINER("container"),;

    final String name;

    ContextName(String contextName) {
        this.name = contextName;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
