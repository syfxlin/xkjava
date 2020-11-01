/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.http.Callable;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.ResponseReturnValueResolver;
import me.ixk.framework.web.WebContext;

/**
 * 可调用返回值解析器
 *
 * @author Otstar Lin
 * @date 2020/11/1 下午 9:41
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE)
public class CallableReturnValueResolver
    implements ResponseReturnValueResolver {

    @Override
    public boolean supportsReturnType(
        Object value,
        MethodReturnValue returnValue
    ) {
        return value instanceof Callable;
    }

    @Override
    public Object resolveReturnValue(
        Object value,
        MethodReturnValue returnValue,
        WebContext context
    ) {
        return ((Callable) value).call();
    }
}
