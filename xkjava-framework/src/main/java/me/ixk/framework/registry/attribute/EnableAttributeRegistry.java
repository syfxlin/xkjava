/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.attribute;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.Enable;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * @author Otstar Lin
 * @date 2020/11/30 上午 11:26
 */
public class EnableAttributeRegistry implements AttributeRegistry {

    @Override
    public Object register(
        final XkJava app,
        final String attributeName,
        final AnnotatedElement element,
        final String scopeType,
        final MergedAnnotation annotation
    ) {
        final Set<String> enableFunctions = app.enableFunctions();
        for (final Enable enable : annotation.getAnnotations(Enable.class)) {
            enableFunctions.addAll(Arrays.asList(enable.name()));
            enableFunctions.addAll(
                Arrays
                    .stream(enable.classes())
                    .map(Class::getName)
                    .collect(Collectors.toSet())
            );
        }
        return enableFunctions;
    }
}
