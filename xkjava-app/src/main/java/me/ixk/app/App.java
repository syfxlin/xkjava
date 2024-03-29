/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app;

import me.ixk.framework.annotation.core.ComponentScan;
import me.ixk.framework.annotation.core.ComponentScan.Filter;
import me.ixk.framework.annotation.core.Enable;
import me.ixk.framework.annotation.core.FilterType;
import me.ixk.framework.annotation.database.MapperScan;
import me.ixk.framework.ioc.XkJava;

/**
 * 启动流程：XkJava.create().boot() -> load() -> booting() -> Bootstrap -> Provider -> booted() -> Start
 * Jetty
 * <p>
 * 请求流程：HttpServlet.service()[Jetty] -> AbstractFrameworkServlet.do* ->
 * AbstractFrameworkServlet.processRequest() -> DispatcherServlet.dispatch() ->
 * RouteManager.dispatch() -> RouteHandler.handle() -> HandlerMiddlewareChain.handle() -> Before
 * Middleware -> Handler.handle() -> ResponseProcessor.toResponse()[View Render, Object wrap] ->
 * After Middleware -> ResponseProcessor.dispatchResponse() -> ***
 * <p>
 * 错误处理：ErrorHandler.handle() 如果是浏览器请求，则返回 HTML 错误页，否则返回 JSON
 */
@MapperScan(basePackages = "me.ixk.app.mapper")
@ComponentScan(
    excludeFilters = {
        @Filter(type = FilterType.REGEX, pattern = "me.ixk.app.beans.User"),
    }
)
@Enable("scheduled")
public class App {

    public static void main(final String[] args) {
        XkJava.boot(App.class, args);
    }
}
