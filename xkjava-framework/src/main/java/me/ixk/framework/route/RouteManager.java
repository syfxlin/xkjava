/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import me.ixk.framework.annotations.Component;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.web.BasicErrorHandler;

/**
 * 路由管理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:51
 */
@Component(name = "routeManager")
public class RouteManager {

    protected final XkJava app;

    protected final RouteParser parser;
    protected final RouteGenerator generator;
    protected final RouteCollector collector;
    protected final RouteDispatcher dispatcher;

    public RouteManager(final XkJava app) {
        this.app = app;
        this.parser = this.app.make(RouteParser.class);
        this.generator = this.app.make(RouteGenerator.class);
        this.collector = this.app.make(RouteCollector.class);
        this.dispatcher = this.app.make(RouteDispatcher.class);
    }

    public Response dispatch(final Request request, final Response response) {
        return this.handleRequest(this.dispatcher, request, response);
    }

    public Response handleRequest(
        final RouteDispatcher dispatcher,
        final Request request,
        final Response response
    ) {
        final RouteInfo routeInfo = dispatcher.dispatch(
            request.method(),
            request.path()
        );

        // 将 Route 信息设置到 Request
        request.setRoute(routeInfo);

        switch (routeInfo.getStatus()) {
            case NOT_FOUND:
                this.app.make(BasicErrorHandler.class)
                    .afterException(
                        new HttpException(
                            HttpStatus.NOT_FOUND,
                            "The URI \"" + request.url() + "\" was not found."
                        ),
                        request,
                        response,
                        routeInfo
                    );
                break;
            case METHOD_NOT_ALLOWED:
                this.app.make(BasicErrorHandler.class)
                    .afterException(
                        new HttpException(
                            HttpStatus.METHOD_NOT_ALLOWED,
                            "Method \"" +
                            request.method() +
                            "\" is not allowed."
                        ),
                        request,
                        response,
                        routeInfo
                    );
                break;
            case FOUND:
                routeInfo.getHandler().handle(routeInfo, request, response);
                break;
            default:
            //
        }
        return response;
    }
}
