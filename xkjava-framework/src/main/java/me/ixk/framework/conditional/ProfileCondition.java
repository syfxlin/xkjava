/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.Profile;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 条件（环境）
 *
 * @author Otstar Lin
 * @date 2020/11/9 上午 10:28
 */
public class ProfileCondition implements Condition {

    @Override
    public boolean matches(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        final String current = app.profile();
        final Profile profile = annotation.getAnnotation(Profile.class);
        for (final String item : profile.value()) {
            if (current.equalsIgnoreCase(item)) {
                return true;
            }
        }
        return false;
    }
}
