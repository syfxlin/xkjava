/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import me.ixk.framework.annotation.condition.ConditionalOnProperty;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 条件（OnProperty）
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:57
 */
public class OnPropertyCondition implements Condition {

    private static final Logger log = LoggerFactory.getLogger(
        OnPropertyCondition.class
    );

    @Override
    public boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    ) {
        ConditionalOnProperty conditional = annotation.getAnnotation(
            ConditionalOnProperty.class
        );
        String prefix = conditional.prefix();
        String havingValue = conditional.havingValue();
        boolean matchIfMissing = conditional.matchIfMissing();
        for (String name : conditional.name()) {
            if (!app.env().has(prefix + name) && !matchIfMissing) {
                log.error("Missing property: {}", prefix + name);
                throw new IllegalArgumentException(
                    "Missing property: " + prefix + name
                );
            }
            if (
                !"".equals(havingValue) &&
                !Objects.equals(havingValue, app.env().get(prefix + name))
            ) {
                return false;
            }
        }
        return true;
    }
}
