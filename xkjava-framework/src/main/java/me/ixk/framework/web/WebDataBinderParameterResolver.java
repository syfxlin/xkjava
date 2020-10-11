/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ValidGroup;
import me.ixk.framework.utils.ValidResult;

public class WebDataBinderParameterResolver
    implements RequestParameterResolver {
    private static final Set<Class<?>> SKIP_TYPES = new HashSet<>(
        Arrays.asList(ValidResult.class, ValidGroup.class)
    );

    @Override
    public boolean supportsParameter(
        final Object value,
        final MethodParameter parameter
    ) {
        if (SKIP_TYPES.contains(parameter.getParameterType())) {
            return false;
        }
        return value == null;
    }

    @Override
    public Object resolveParameter(
        final Object value,
        final MethodParameter parameter,
        final WebContext context,
        final WebDataBinder binder
    ) {
        final DataBind dataBind = AnnotationUtils
            .getAnnotation(parameter.getParameter())
            .getAnnotation(DataBind.class);
        Object dependency = binder.getObject(
            parameter.getParameterName(),
            parameter.getParameterType(),
            dataBind
        );
        if (dependency == null && dataBind != null && dataBind.required()) {
            throw new NullPointerException(
                "Target [" +
                parameter.getControllerClass().getName() +
                "@" +
                parameter.getMethod().getName() +
                "(" +
                parameter.getParameterName() +
                ")] is required, but inject value is null"
            );
        }
        return dependency;
    }
}
