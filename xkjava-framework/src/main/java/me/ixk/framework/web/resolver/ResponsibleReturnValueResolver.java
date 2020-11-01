/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.http.Responsible;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.ResponseReturnValueResolver;
import me.ixk.framework.web.WebContext;

/**
 * 可响应返回值解析器
 *
 * @author Otstar Lin
 * @date 2020/11/1 下午 9:22
 */
@WebResolver
public class ResponsibleReturnValueResolver
    implements ResponseReturnValueResolver {

    @Override
    public boolean supportsReturnType(
        Object value,
        MethodReturnValue returnValue
    ) {
        return value instanceof Responsible;
    }

    @Override
    public Object resolveReturnValue(
        Object value,
        MethodReturnValue returnValue,
        WebContext context
    ) {
        return ((Responsible) value).toResponse(
                context.getRequest(),
                context.getResponse(),
                value
            );
    }
}
