/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;

public class FalseCondition implements Condition {

    @Override
    public boolean matches(
        final XkJava app,
        final AnnotatedElement element,
        MergeAnnotation annotation
    ) {
        return false;
    }
}
