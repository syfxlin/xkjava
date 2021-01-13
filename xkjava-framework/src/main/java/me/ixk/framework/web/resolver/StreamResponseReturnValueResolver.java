/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import java.io.InputStream;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.http.result.StreamResult;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;

/**
 * Stream 响应解析器
 *
 * @author Otstar Lin
 * @date 2020/12/20 下午 5:54
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE)
public class StreamResponseReturnValueResolver
    implements ResponseReturnValueResolver {

    @Override
    public boolean supportsReturnType(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        return value instanceof StreamResult || value instanceof InputStream;
    }

    @Override
    public Object resolveReturnValue(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        final StreamResult result;
        if (value instanceof StreamResult) {
            result = (StreamResult) value;
        } else if (value instanceof InputStream) {
            result = new StreamResult((InputStream) value);
        } else {
            return value;
        }
        if (!result.async()) {
            return result;
        }
        context
            .getAsyncManager()
            .startAsync(
                () -> {
                    result.toResponse(
                        context.getRequest(),
                        context.getResponse(),
                        value
                    );
                    return null;
                }
            );
        return null;
    }
}
