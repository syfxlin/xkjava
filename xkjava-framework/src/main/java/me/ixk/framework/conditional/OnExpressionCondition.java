/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotation.ConditionalOnExpression;
import me.ixk.framework.expression.BeanExpressionResolver;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.Convert;
import me.ixk.framework.util.MergedAnnotation;

/**
 * 条件（OnExpression）
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:56
 */
public class OnExpressionCondition implements Condition {

    @Override
    public boolean matches(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        return Convert.convert(
            Boolean.class,
            app
                .make(BeanExpressionResolver.class)
                .evaluate(
                    annotation.getString(
                        ConditionalOnExpression.class,
                        "value"
                    ),
                    Boolean.class
                )
        );
    }
}
