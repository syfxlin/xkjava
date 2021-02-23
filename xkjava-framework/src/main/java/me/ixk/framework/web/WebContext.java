/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.annotation.core.Scope;
import me.ixk.framework.http.CookieManager;
import me.ixk.framework.http.Model;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SessionManager;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.property.Environment;
import me.ixk.framework.servlet.DispatcherServlet;
import me.ixk.framework.web.async.WebAsyncManager;

/**
 * Web 上下文
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:22
 */
@Component(name = "webContext")
@Scope(ScopeType.REQUEST)
public class WebContext {

    private final XkJava app;
    private final Environment environment;
    private final DispatcherServlet servlet;
    private final Request request;
    private final Response response;
    private final CookieManager cookieManager;
    private final SessionManager sessionManager;
    private final WebAsyncManager asyncManager;
    private final Model model;

    @Deprecated
    public WebContext() {
        // only cglib
        this.app = null;
        this.environment = null;
        this.servlet = null;
        this.request = null;
        this.response = null;
        this.cookieManager = null;
        this.sessionManager = null;
        this.asyncManager = null;
        this.model = null;
    }

    public WebContext(final XkJava app) {
        this.app = app;
        this.environment = app.make(Environment.class);
        this.servlet = app.make(DispatcherServlet.class);
        this.request = app.make(Request.class);
        this.response = app.make(Response.class);
        this.cookieManager = app.make(CookieManager.class);
        this.sessionManager = app.make(SessionManager.class);
        this.asyncManager = app.make(WebAsyncManager.class);
        this.model = app.make(Model.class);
    }

    public XkJava app() {
        return app;
    }

    public Environment env() {
        return environment;
    }

    public DispatcherServlet servlet() {
        return servlet;
    }

    public Request request() {
        return request;
    }

    public Response response() {
        return response;
    }

    public CookieManager cookie() {
        return cookieManager;
    }

    public SessionManager session() {
        return sessionManager;
    }

    public WebAsyncManager async() {
        return asyncManager;
    }

    public Model model() {
        return model;
    }
}
