/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import cn.hutool.core.io.resource.ResourceUtil;
import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotation.condition.ConditionalOnResource;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 条件（OnResource）
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:57
 */
public class OnResourceCondition implements Condition {

    private static final Logger log = LoggerFactory.getLogger(
        OnResourceCondition.class
    );

    @Override
    public boolean matches(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        for (final String resource : (String[]) annotation.get(
            ConditionalOnResource.class,
            "resources"
        )) {
            if (ResourceUtil.getResource(resource) == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Missing resource: {}", resource);
                }
                return false;
            }
        }
        return true;
    }
}
