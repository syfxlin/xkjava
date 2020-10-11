/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

public interface RequestParametersPostResolver {
    boolean supportsParameters(Object[] parameters, MethodParameter parameter);

    Object[] resolveParameters(
        Object[] parameters,
        MethodParameter parameter,
        WebContext context,
        WebDataBinder binder
    );
}
