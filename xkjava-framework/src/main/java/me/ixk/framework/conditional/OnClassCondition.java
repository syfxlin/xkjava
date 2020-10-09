/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ClassUtil;
import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.ConditionalOnClass;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnClassCondition implements Condition {
    private static final Logger log = LoggerFactory.getLogger(
        OnClassCondition.class
    );

    @Override
    public boolean matches(
        final XkJava app,
        final AnnotatedElement element,
        final MergeAnnotation annotation
    ) {
        boolean match =
            annotation.getAnnotation(ConditionalOnClass.class) != null;
        String msg = match ? "Missing class: {}" : "Visible class: {}";
        for (Class<?> clazz : (Class<?>[]) annotation.get("value")) {
            try {
                ClassUtil.loadClass(clazz.getName());
            } catch (UtilException e) {
                log.debug(msg, clazz.getName());
                return !match;
            }
        }
        for (String name : (String[]) annotation.get("name")) {
            try {
                ClassUtil.loadClass(name);
            } catch (UtilException e) {
                log.debug(msg, name);
                return !match;
            }
        }
        return match;
    }
}
