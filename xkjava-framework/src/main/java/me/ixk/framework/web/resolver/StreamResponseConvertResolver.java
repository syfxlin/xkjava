/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import java.io.InputStream;
import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.http.Response;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;

/**
 * Stream 响应解析器
 *
 * @author Otstar Lin
 * @date 2020/12/20 下午 5:54
 */
@WebResolver
public class StreamResponseConvertResolver implements ResponseConvertResolver {

    @Override
    public boolean supportsConvert(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context,
        final RouteInfo info
    ) {
        return value instanceof InputStream;
    }

    @Override
    public Response resolveConvert(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context,
        final RouteInfo info
    ) {
        final Response response = context.getResponse();
        response.content((InputStream) value);
        return response;
    }
}
