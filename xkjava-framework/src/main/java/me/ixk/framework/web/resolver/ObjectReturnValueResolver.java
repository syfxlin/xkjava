/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.utils.JSON;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.ResponseReturnValueResolver;
import me.ixk.framework.web.WebContext;

/**
 * 对象响应解析器
 *
 * @author Otstar Lin
 * @date 2020/11/1 下午 9:48
 */
@WebResolver
@Order(Order.LOWEST_PRECEDENCE)
public class ObjectReturnValueResolver implements ResponseReturnValueResolver {

    @Override
    public boolean supportsReturnType(
        Object value,
        MethodReturnValue returnValue
    ) {
        return !(value instanceof HttpServletResponse);
    }

    @Override
    public Object resolveReturnValue(
        Object value,
        MethodReturnValue returnValue,
        WebContext context
    ) {
        return context
            .getResponse()
            .contentType(MimeType.APPLICATION_JSON)
            .content(JSON.stringify(value));
    }
}
