/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.JavaVersion;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;

public class OnJavaCondition implements Condition {

    @Override
    public boolean matches(
        final XkJava app,
        final AnnotatedElement element,
        final MergeAnnotation annotation
    ) {
        JavaVersion version = annotation.get("version");
        return (
            version == null ||
            version.isEqualOrNewerThan(JavaVersion.getCurrentVersion())
        );
    }
}
