/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

@FunctionalInterface
public interface Wrapper {
    Object getInstance(Container container, With with) throws Throwable;
}
