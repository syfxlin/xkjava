/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

@FunctionalInterface
public interface MethodInjector {
    Object inject(Binding binding, Object instance, With with);
}
