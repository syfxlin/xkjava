/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app;

import me.ixk.framework.ioc.XkJava;

/**
 * 启动流程：XkJava.create().boot() -> load() -> booting()
 *         -> Bootstrap -> Provider -> booted() -> Start Jetty
 *
 * 请求流程：HttpServlet.service()[Jetty] -> FrameworkServlet.do* -> FrameworkServlet.processRequest()
 *         -> DispatcherServlet.dispatch() -> RouteManager.dispatch() -> RouteHandler.handle()
 *         -> Runner.handle() -> Before Middleware -> Handler.handle() -> ResponseProcessor.toResponse()[View Render, Object wrap]
 *         -> After Middleware -> ResponseProcessor.dispatchResponse() -> ***
 *
 * 错误处理：ErrorHandler.handle() 如果是浏览器请求，则返回 HTML 错误页，否则返回 JSON
 */
public class App {

    public static void main(String[] args) {
        XkJava.create().boot(App.class, args);
    }
}
