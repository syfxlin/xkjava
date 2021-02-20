/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotation.ConditionalOnJava;
import me.ixk.framework.annotation.JavaVersion;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;

/**
 * 条件（OnJava）
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:57
 */
public class OnJavaCondition implements Condition {

    @Override
    public boolean matches(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        JavaVersion version = annotation.getEnum(
            ConditionalOnJava.class,
            "version",
            JavaVersion.class
        );
        return (
            version == null ||
            version.isEqualOrNewerThan(JavaVersion.getCurrentVersion())
        );
    }
}
