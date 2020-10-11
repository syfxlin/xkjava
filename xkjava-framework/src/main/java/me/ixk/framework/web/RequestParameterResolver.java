/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

public interface RequestParameterResolver {
    boolean supportsParameter(Object value, MethodParameter parameter);

    Object resolveParameter(
        Object value,
        MethodParameter parameter,
        WebContext context,
        WebDataBinder binder
    );
}
