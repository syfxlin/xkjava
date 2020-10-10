/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;

public interface BeforeImportBeanRegistrar {
    void before(
        XkJava app,
        AnnotatedElement element,
        MergeAnnotation annotation
    );
}
