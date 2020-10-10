/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;

public interface AttributeRegistrar {
    Object register(
        XkJava app,
        String attributeName,
        AnnotatedElement element,
        ScopeType scopeType,
        MergeAnnotation annotation
    );
}
