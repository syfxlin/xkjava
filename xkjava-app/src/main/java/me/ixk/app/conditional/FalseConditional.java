/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;

public class FalseConditional implements Condition {

    @Override
    public boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergeAnnotation annotation
    ) {
        return false;
    }
}
