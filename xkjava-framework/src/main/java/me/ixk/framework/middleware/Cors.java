/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import java.util.Arrays;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.CrossOrigin;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RequestMethod;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

@Order(Order.HIGHEST_PRECEDENCE)
public class Cors implements Middleware {

    @Override
    public Response handle(final Request request, final Runner next) {
        final Response response = next.handle(request);
        final CrossOrigin crossOrigin = (CrossOrigin) request.getAttribute(
            "me.ixk.framework.annotation.CrossOrigin"
        );
        String origin;
        if (CrossOrigin.DYNAMIC_ORIGIN.equals(crossOrigin.origins())) {
            origin = request.header("Origin");
        } else {
            origin = crossOrigin.origins();
        }
        if (origin != null && !origin.isEmpty()) {
            response.header("Access-Control-Allow-Origin", origin);
        }
        if (!crossOrigin.allowCredentials().isEmpty()) {
            response.header(
                "Access-Control-Allow-Credentials",
                crossOrigin.allowCredentials()
            );
        }
        String methods = "*";
        if (crossOrigin.methods().length > 0) {
            methods =
                Arrays
                    .stream(crossOrigin.methods())
                    .map(RequestMethod::toString)
                    .collect(Collectors.joining(","));
        }
        response.header("Access-Control-Allow-Methods", methods);
        String headers = "*";
        if (crossOrigin.allowedHeaders().length > 0) {
            headers = String.join(",", crossOrigin.allowedHeaders());
        }
        response.header("Access-Control-Allow-Headers", headers);
        return response;
    }
}
