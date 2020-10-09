/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.utils.MergeAnnotation;

@FunctionalInterface
public interface Condition {
    boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergeAnnotation annotation
    );
}