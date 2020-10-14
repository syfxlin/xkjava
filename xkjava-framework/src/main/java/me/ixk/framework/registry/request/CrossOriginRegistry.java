/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.request;

import java.lang.reflect.Method;
import me.ixk.framework.annotations.CrossOrigin;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * CrossOriginRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 2:05
 */
public class CrossOriginRegistry implements RequestAttributeRegistry {

    @Override
    public Object register(
        XkJava app,
        String attributeName,
        Method method,
        MergedAnnotation annotation
    ) {
        return annotation.getAnnotation(CrossOrigin.class);
    }
}
