/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.ConditionalOnBean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 条件（OnBean）
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:55
 */
public class OnBeanCondition implements Condition {
    private static final Logger log = LoggerFactory.getLogger(
        OnBeanCondition.class
    );

    @Override
    public boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    ) {
        boolean match = annotation.hasAnnotation(ConditionalOnBean.class);
        String msg = match ? "Missing bean: {}" : "Visible bean: {}";
        Class<? extends Annotation> type = match
            ? ConditionalOnBean.class
            : ConditionalOnMissingBean.class;
        for (Class<?> beanType : (Class<?>[]) annotation.get(type, "value")) {
            if (!app.hasBinding(beanType)) {
                log.debug(msg, beanType.getName());
                return !match;
            }
        }
        for (String beanName : (String[]) annotation.get(type, "name")) {
            if (!app.hasBinding(beanName)) {
                log.debug(msg, beanName);
                return !match;
            }
        }
        return match;
    }
}
