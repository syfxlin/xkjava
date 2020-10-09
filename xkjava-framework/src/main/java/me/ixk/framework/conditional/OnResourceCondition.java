/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import cn.hutool.core.io.resource.ResourceUtil;
import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnResourceCondition implements Condition {
    private static final Logger log = LoggerFactory.getLogger(
        OnResourceCondition.class
    );

    @Override
    public boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergeAnnotation annotation
    ) {
        for (String resource : (String[]) annotation.get("resources")) {
            if (ResourceUtil.getResource(resource) == null) {
                log.debug("Missing resource: {}", resource);
                return false;
            }
        }
        return true;
    }
}