/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

public interface ImportBeanRegistrar {
    Binding register(
        XkJava app,
        Class<?> clazz,
        ScopeType scopeType,
        MergedAnnotation annotation
    );
}
