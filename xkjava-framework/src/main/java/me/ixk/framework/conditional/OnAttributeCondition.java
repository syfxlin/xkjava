/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.ConditionalOnAttribute;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnAttributeCondition implements Condition {
    private static final Logger log = LoggerFactory.getLogger(
        OnAttributeCondition.class
    );

    @Override
    public boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergeAnnotation annotation
    ) {
        boolean match =
            annotation.getAnnotation(ConditionalOnAttribute.class) != null;
        String msg = match ? "Missing attribute: {}" : "Visible attribute: {}";
        for (String beanName : (String[]) annotation.get("name")) {
            if (!app.hasAttribute(beanName)) {
                log.debug(msg, beanName);
                return !match;
            }
        }
        return match;
    }
}
