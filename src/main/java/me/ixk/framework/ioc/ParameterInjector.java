/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.lang.reflect.Executable;

@FunctionalInterface
public interface ParameterInjector {
    Object[] inject(Binding binding, Executable method, With with);
}
