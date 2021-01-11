/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import java.io.InputStream;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.http.result.StreamResult;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.web.WebContext;

/**
 * Stream 响应解析器
 *
 * @author Otstar Lin
 * @date 2020/12/20 下午 5:54
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE)
public class StreamResponseConvertResolver implements ResponseConvertResolver {

    @Override
    public boolean supportsConvert(
        final Object value,
        final WebContext context,
        final RouteInfo info
    ) {
        return value instanceof StreamResult || value instanceof InputStream;
    }

    @Override
    public boolean resolveConvert(
        final Object value,
        final WebContext context,
        final RouteInfo info
    ) {
        final StreamResult result;
        if (value instanceof StreamResult) {
            result = (StreamResult) value;
        } else if (value instanceof InputStream) {
            result = new StreamResult((InputStream) value);
        } else {
            return false;
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
                }
            );
        return true;
    }
}
