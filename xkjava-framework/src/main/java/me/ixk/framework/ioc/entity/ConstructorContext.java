/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.entity;

import java.lang.reflect.Constructor;

/**
 * 构造函数上下文
 *
 * @author Otstar Lin
 * @date 2020/12/24 下午 2:41
 */
public class ConstructorContext {

    private final Constructor<?> constructor;
    private final Object[] args;

    public ConstructorContext(
        final Constructor<?> constructor,
        final Object[] args
    ) {
        this.constructor = constructor;
        this.args = args;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public Object[] getArgs() {
        return args;
    }
}
