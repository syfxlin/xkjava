/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import javax.el.PropertyNotFoundException;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnPropertyCondition implements Condition {
    private static final Logger log = LoggerFactory.getLogger(
        OnPropertyCondition.class
    );

    @Override
    public boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergeAnnotation annotation
    ) {
        String prefix = annotation.get("prefix");
        String havingValue = annotation.get("havingValue");
        boolean matchIfMissing = annotation.get("matchIfMissing");
        for (String name : (String[]) annotation.get("name")) {
            if (!app.env().has(prefix + name) && !matchIfMissing) {
                log.error("Missing property: {}", prefix + name);
                throw new PropertyNotFoundException(
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
