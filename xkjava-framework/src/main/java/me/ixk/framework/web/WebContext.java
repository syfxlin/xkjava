/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.http.CookieManager;
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

    private final XkJava application;
    private final Environment environment;
    private final DispatcherServlet servlet;
    private final Request request;
    private final Response response;
    private final CookieManager cookieManager;
    private final SessionManager sessionManager;
    private final WebAsyncManager asyncManager;

    @Deprecated
    public WebContext() {
        // only cglib
        this.application = null;
        this.environment = null;
        this.servlet = null;
        this.request = null;
        this.response = null;
        this.cookieManager = null;
        this.sessionManager = null;
        this.asyncManager = null;
    }

    public WebContext(final XkJava application) {
        this.application = application;
        this.environment = application.make(Environment.class);
        this.servlet = application.make(DispatcherServlet.class);
        this.request = application.make(Request.class);
        this.response = application.make(Response.class);
        this.cookieManager = application.make(CookieManager.class);
        this.sessionManager = application.make(SessionManager.class);
        this.asyncManager = application.make(WebAsyncManager.class);
    }

    public XkJava getApplication() {
        return application;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public DispatcherServlet getServlet() {
        return servlet;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public WebAsyncManager getAsyncManager() {
        return asyncManager;
    }
}
