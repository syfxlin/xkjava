/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.ConditionalOnExpression;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.Express;
import me.ixk.framework.utils.MergedAnnotation;

public class OnExpressionCondition implements Condition {

    @Override
    public boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    ) {
        return Convert.convert(
            Boolean.class,
            Express.evaluateApp(
                annotation.get(ConditionalOnExpression.class, "value"),
                Boolean.class
            )
        );
    }
}
