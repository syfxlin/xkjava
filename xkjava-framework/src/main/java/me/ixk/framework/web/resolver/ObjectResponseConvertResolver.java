/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.http.Response;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.utils.Json;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;

/**
 * 对象响应解析器
 *
 * @author Otstar Lin
 * @date 2020/11/1 下午 9:48
 */
@WebResolver
@Order(Order.LOWEST_PRECEDENCE)
public class ObjectResponseConvertResolver implements ResponseConvertResolver {

    @Override
    public boolean supportsConvert(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context,
        final RouteInfo info
    ) {
        return Json.make().canSerialize(value.getClass());
    }

    @Override
    public Response resolveConvert(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context,
        final RouteInfo info
    ) {
        return context
            .getResponse()
            .contentType(MimeType.APPLICATION_JSON)
            .content(Json.stringify(value));
    }
}
