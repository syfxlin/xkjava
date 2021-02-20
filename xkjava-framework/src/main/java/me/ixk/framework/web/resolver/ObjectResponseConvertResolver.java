/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import me.ixk.framework.annotation.Order;
import me.ixk.framework.annotation.WebResolver;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.util.Json;
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
        final WebContext context,
        final RouteInfo info
    ) {
        return Json.make().canSerialize(value.getClass());
    }

    @Override
    public boolean resolveConvert(
        final Object value,
        final WebContext context,
        final RouteInfo info
    ) {
        context
            .getResponse()
            .contentType(MimeType.APPLICATION_JSON)
            .content(Json.stringify(value));
        return true;
    }
}
