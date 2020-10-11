/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.util.Map;
import java.util.Map.Entry;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.ResponseProcessor;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.web.RequestAttributeRegistry;
import me.ixk.framework.web.RequestAttributeRegistry.RequestAttributeDefinition;

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
        final RouteResult routeResult = dispatcher.dispatch(
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
                handleFound(routeResult.getHandler(), request, response);
                break;
            default:
            //
        }
        return ResponseProcessor.dispatchResponse(response);
    }

    public void handleFound(
        RouteHandler handler,
        Request request,
        Response response
    ) {
        RequestAttributeRegistry registry =
            this.app.make(RequestAttributeRegistry.class);
        Map<String, RequestAttributeDefinition> registrar = registry.getRegistrar(
            handler.getMethod()
        );
        if (registrar != null) {
            for (Entry<String, RequestAttributeDefinition> entry : registrar.entrySet()) {
                String attributeName = entry.getKey();
                RequestAttributeDefinition definition = entry.getValue();
                request.setAttribute(
                    attributeName,
                    definition
                        .getRegistrar()
                        .register(
                            this.app,
                            attributeName,
                            definition.getMethod(),
                            definition.getAnnotation()
                        )
                );
            }
        }
        handler.handle(request, response);
    }
}
