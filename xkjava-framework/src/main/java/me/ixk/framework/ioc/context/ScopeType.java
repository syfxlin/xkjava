/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

/**
 * 作用域类型
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:48
 */
public final class ScopeType {

    /**
     * 单例
     */
    public static final String SINGLETON = "singleton";
    /**
     * 多例
     */
    public static final String PROTOTYPE = "prototype";
    /**
     * 请求
     */
    public static final String REQUEST = "request";
    /**
     * 会话
     */
    public static final String SESSION = "session";
}
