/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.ConditionalOnAttribute;
import me.ixk.framework.annotations.ConditionalOnMissingAttribute;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;
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
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    ) {
        boolean match = annotation.hasAnnotation(ConditionalOnAttribute.class);
        String msg = match ? "Missing attribute: {}" : "Visible attribute: {}";
        for (String beanName : (String[]) annotation.get(
            match
                ? ConditionalOnAttribute.class
                : ConditionalOnMissingAttribute.class,
            "name"
        )) {
            if (!app.hasAttribute(beanName)) {
                log.debug(msg, beanName);
                return !match;
            }
        }
        return match;
    }
}
