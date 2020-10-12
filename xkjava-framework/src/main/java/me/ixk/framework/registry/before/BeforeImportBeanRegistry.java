/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.before;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

public interface BeforeImportBeanRegistry {
    void before(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    );
}
