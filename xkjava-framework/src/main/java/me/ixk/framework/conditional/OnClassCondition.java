/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotation.condition.ConditionalOnClass;
import me.ixk.framework.annotation.condition.ConditionalOnMissingClass;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 条件（OnClass）
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:55
 */
public class OnClassCondition implements Condition {

    private static final Logger log = LoggerFactory.getLogger(
        OnClassCondition.class
    );

    @Override
    public boolean matches(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        final boolean match = annotation.hasAnnotation(
            ConditionalOnClass.class
        );
        final Class<? extends Annotation> type = match
            ? ConditionalOnClass.class
            : ConditionalOnMissingClass.class;
        final String msg = match ? "Missing class: {}" : "Visible class: {}";
        for (final Class<?> clazz : (Class<?>[]) annotation.get(
            type,
            "value"
        )) {
            try {
                ClassUtil.loadClass(clazz.getName());
            } catch (final UtilException e) {
                if (log.isDebugEnabled()) {
                    log.debug(msg, clazz.getName());
                }
                return !match;
            }
        }
        for (final String name : (String[]) annotation.get(type, "name")) {
            try {
                ClassUtil.loadClass(name);
            } catch (final UtilException e) {
                if (log.isDebugEnabled()) {
                    log.debug(msg, name);
                }
                return !match;
            }
        }
        return match;
    }
}
