/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import me.ixk.framework.annotations.CrossOrigin;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.web.CorsProcessor;

/**
 * CORS
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:42
 */
@Order(Order.HIGHEST_PRECEDENCE)
public class Cors implements Middleware {
    private final CorsProcessor corsProcessor;

    public Cors(CorsProcessor corsProcessor) {
        this.corsProcessor = corsProcessor;
    }

    @Override
    public Response handle(final Request request, final Runner next) {
        final Response response = next.handle(request);
        final CrossOrigin crossOrigin = (CrossOrigin) request.getAttribute(
            "me.ixk.framework.annotation.CrossOrigin"
        );
        this.corsProcessor.processRequest(crossOrigin, request, response);
        return response;
    }
}
