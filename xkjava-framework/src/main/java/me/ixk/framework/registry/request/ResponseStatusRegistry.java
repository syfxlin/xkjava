/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.request;

import java.lang.reflect.Method;
import me.ixk.framework.annotations.ResponseStatus;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

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
