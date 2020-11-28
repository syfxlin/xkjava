/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.ConditionalOnExpression;
import me.ixk.framework.expression.BeanExpressionResolver;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 条件（OnExpression）
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:56
 */
public class OnExpressionCondition implements Condition {

    @Override
    public boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    ) {
        return Convert.convert(
            Boolean.class,
            app
                .make(BeanExpressionResolver.class)
                .evaluate(
                    annotation.get(ConditionalOnExpression.class, "value"),
                    Boolean.class
                )
        );
    }
}
