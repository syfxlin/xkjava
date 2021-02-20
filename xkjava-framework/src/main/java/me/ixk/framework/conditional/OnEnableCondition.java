/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import java.util.Set;
import me.ixk.framework.annotation.ConditionalOnEnable;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;

/**
 * 条件（OnEnable）
 *
 * @author Otstar Lin
 * @date 2020/11/30 上午 11:38
 */
public class OnEnableCondition implements Condition {

    @Override
    public boolean matches(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        final Set<String> enableFunctions = app.enableFunctions();
        final ConditionalOnEnable onEnable = annotation.getAnnotation(
            ConditionalOnEnable.class
        );
        for (final String name : onEnable.name()) {
            if (!enableFunctions.contains(name)) {
                return false;
            }
        }
        for (final Class<?> clazz : onEnable.classes()) {
            if (!enableFunctions.contains(clazz.getName())) {
                return false;
            }
        }
        return true;
    }
}
