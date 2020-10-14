/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

/**
 * 回调
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:15
 */
@FunctionalInterface
public interface Callback {
    /**
     * 调用
     *
     * @param app 应用
     *
     * @return 返回值
     */
    Object invoke(XkJava app);
}
