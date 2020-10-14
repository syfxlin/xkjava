/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

/**
 * 响应码
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:44
 */
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class ResponseStatus implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        final Response response = next.handle(request);
        me.ixk.framework.annotations.ResponseStatus responseStatus = (me.ixk.framework.annotations.ResponseStatus) request.getAttribute(
            "me.ixk.framework.annotations.ResponseStatus"
        );
        final HttpStatus status = responseStatus.code();
        if (responseStatus.reason().isEmpty()) {
            return response.status(status);
        }
        return response.status(status.getValue(), responseStatus.reason());
    }
}
