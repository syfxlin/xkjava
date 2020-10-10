/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

public interface AfterImportBeanRegistrar {
    void after(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation,
        Binding binding
    );
}
