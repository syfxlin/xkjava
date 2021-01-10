/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;

/**
 * ResponseReturnValueResolver
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:20
 */
public interface ResponseReturnValueResolver {
    /**
     * 是否支持
     *
     * @param value       返回值
     * @param returnValue 返回值信息
     * @param context     Web 上下文
     * @return 是否支持
     */
    boolean supportsReturnType(
        Object value,
        MethodReturnValue returnValue,
        WebContext context
    );

    /**
     * 解析返回值
     *
     * @param value       返回值
     * @param returnValue 返回值信息
     * @param context     Web 上下文
     * @return 返回值
     */
    Object resolveReturnValue(
        Object value,
        MethodReturnValue returnValue,
        WebContext context
    );
}
