/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.route.RouteInfo;

/**
 * 响应码
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:44
 */
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class ResponseStatus implements Middleware {

    @Override
    public Object handle(
        Request request,
        Response response,
        MiddlewareChain next,
        RouteInfo info
    ) {
        final Object value = next.handle(request, response);
        me.ixk.framework.annotations.ResponseStatus responseStatus = (me.ixk.framework.annotations.ResponseStatus) request.getAttribute(
            "me.ixk.framework.annotations.ResponseStatus"
        );
        final HttpStatus status = responseStatus.code();
        if (responseStatus.reason().isEmpty()) {
            response.status(status);
        }
        response.status(status.getValue(), responseStatus.reason());
        return value;
    }
}
