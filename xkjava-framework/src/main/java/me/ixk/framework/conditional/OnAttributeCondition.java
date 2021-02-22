/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotation.condition.ConditionalOnAttribute;
import me.ixk.framework.annotation.condition.ConditionalOnMissingAttribute;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 条件（OnAttribute）
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:55
 */
public class OnAttributeCondition implements Condition {

    private static final Logger log = LoggerFactory.getLogger(
        OnAttributeCondition.class
    );

    @Override
    public boolean matches(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        final boolean match = annotation.hasAnnotation(
            ConditionalOnAttribute.class
        );
        final String msg = match
            ? "Missing attribute: {}"
            : "Visible attribute: {}";
        for (final String beanName : (String[]) annotation.get(
            match
                ? ConditionalOnAttribute.class
                : ConditionalOnMissingAttribute.class,
            "name"
        )) {
            if (!app.hasAttribute(beanName)) {
                if (log.isDebugEnabled()) {
                    log.debug(msg, beanName);
                }
                return !match;
            }
        }
        return match;
    }
}
