/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.lang.reflect.Method;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

public interface RequestAttributeRegistrar {
    Object register(
        XkJava app,
        String attributeName,
        Method method,
        MergedAnnotation annotation
    );
}
