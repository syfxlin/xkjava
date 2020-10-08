/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import me.ixk.framework.annotations.Component;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.ResponseProcessor;
import me.ixk.framework.ioc.XkJava;

@Component(name = "routeManager")
public class RouteManager {
    protected final XkJava app;

    protected final RouteParser parser;
    protected final RouteGenerator generator;
    protected final RouteCollector collector;
    protected final RouteDispatcher dispatcher;

    public RouteManager(XkJava app) {
        this.app = app;
        this.parser = this.app.make(RouteParser.class);
        this.generator = this.app.make(RouteGenerator.class);
        this.collector = this.app.make(RouteCollector.class);
        this.dispatcher = this.app.make(RouteDispatcher.class);
    }

    public Response dispatch(Request request, Response response) {
        return this.handleRequest(this.dispatcher, request, response);
    }

    public Response handleRequest(
        RouteDispatcher dispatcher,
        Request request,
        Response response
    ) {
        RouteResult routeResult = dispatcher.dispatch(
            request.getMethod(),
            request.getHttpURI().getPath()
        );

        // 将 Route 信息设置到 Request
        request.setRoute(routeResult);

        switch (routeResult.getStatus()) {
            case NOT_FOUND:
                throw new HttpException(
                    HttpStatus.NOT_FOUND,
                    "The URI \"" + request.getHttpURI() + "\" was not found."
                );
            case METHOD_NOT_ALLOWED:
                throw new HttpException(
                    HttpStatus.METHOD_NOT_ALLOWED,
                    "Method \"" + request.getMethod() + "\" is not allowed."
                );
            case FOUND:
                routeResult.getHandler().handle(request, response);
                break;
            default:
            //
        }
        return ResponseProcessor.dispatchResponse(response);
    }
}
