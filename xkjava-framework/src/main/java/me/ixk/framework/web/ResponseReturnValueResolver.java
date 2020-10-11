/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

public interface ResponseReturnValueResolver {
    boolean supportsReturnType(Object value, MethodReturnValue returnValue);

    Object resolveReturnValue(
        Object value,
        MethodReturnValue returnValue,
        WebContext context
    );
}
