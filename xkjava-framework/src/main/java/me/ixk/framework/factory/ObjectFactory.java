/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.factory;

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject();
}
