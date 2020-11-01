/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

/**
 * 可调用
 *
 * @author Otstar Lin
 * @date 2020/11/1 下午 9:37
 */
@FunctionalInterface
public interface Callable {
    /**
     * 调用
     *
     * @return 响应
     */
    Object call();
}
