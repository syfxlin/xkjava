/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import java.lang.reflect.Method;

/**
 * 键生成器
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 2:20
 */
public interface KeyGenerator {
    /**
     * 生成键
     *
     * @param target 实例
     * @param method 方法
     * @param params 参数
     *
     * @return 键
     */
    Object generateKey(Object target, Method method, Object... params);
}
