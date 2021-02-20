/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.request;

import java.lang.reflect.Method;
import me.ixk.framework.annotation.ResponseStatus;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;

/**
 * ResponseStatusRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 2:06
 */
public class ResponseStatusRegistry implements RequestAttributeRegistry {

    @Override
    public Object register(
        XkJava app,
        String attributeName,
        Method method,
        MergedAnnotation annotation
    ) {
        return annotation.getAnnotation(ResponseStatus.class);
    }
}
