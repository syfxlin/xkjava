/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.lang.reflect.Method;

public interface Condition {
    boolean matches(XkJava app, Class<?> type);

    boolean matches(XkJava app, Method method);
}
