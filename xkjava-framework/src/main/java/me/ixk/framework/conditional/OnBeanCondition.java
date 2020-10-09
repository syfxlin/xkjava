/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.ConditionalOnBean;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnBeanCondition implements Condition {
    private static final Logger log = LoggerFactory.getLogger(
        OnBeanCondition.class
    );

    @Override
    public boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergeAnnotation annotation
    ) {
        boolean match =
            annotation.getAnnotation(ConditionalOnBean.class) != null;
        String msg = match ? "Missing bean: {}" : "Visible bean: {}";
        for (Class<?> beanType : (Class<?>[]) annotation.get("value")) {
            if (!app.hasBinding(beanType)) {
                log.debug(msg, beanType.getName());
                return !match;
            }
        }
        for (String beanName : (String[]) annotation.get("name")) {
            if (!app.hasBinding(beanName)) {
                log.debug(msg, beanName);
                return !match;
            }
        }
        return match;
    }
}
