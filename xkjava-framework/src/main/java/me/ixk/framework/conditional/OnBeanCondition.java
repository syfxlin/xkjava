/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotation.ConditionalOnBean;
import me.ixk.framework.annotation.ConditionalOnMissingBean;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;
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
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        final boolean match = annotation.hasAnnotation(ConditionalOnBean.class);
        final String msg = match ? "Missing bean: {}" : "Visible bean: {}";
        final Class<? extends Annotation> type = match
            ? ConditionalOnBean.class
            : ConditionalOnMissingBean.class;
        for (final Class<?> beanType : (Class<?>[]) annotation.get(
            type,
            "value"
        )) {
            if (!app.has(beanType)) {
                if (log.isDebugEnabled()) {
                    log.debug(msg, beanType.getName());
                }
                return !match;
            }
        }
        for (final String beanName : (String[]) annotation.get(type, "name")) {
            if (!app.has(beanName)) {
                if (log.isDebugEnabled()) {
                    log.debug(msg, beanName);
                }
                return !match;
            }
        }
        return match;
    }
}
