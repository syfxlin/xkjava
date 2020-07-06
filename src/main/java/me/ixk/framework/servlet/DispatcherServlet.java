/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.http.*;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.ioc.context.ContextName;
import me.ixk.framework.ioc.context.RequestContext;
import me.ixk.framework.kernel.Auth;
import me.ixk.framework.route.RouteManager;

@WebServlet(urlPatterns = "/*")
@MultipartConfig
public class DispatcherServlet extends FrameworkServlet {
    protected final Application app;
    protected final RequestContext requestContext;

    @Deprecated
    public DispatcherServlet() {
        super();
        this.app = Application.get();
        this.requestContext =
            (RequestContext) this.app.getContextByName(
                    ContextName.REQUEST.getName()
                );
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void dispatch(Request request, Response response) {
        try {
            this.beforeDispatch(request, response);
            this.doDispatch(request, response);
        } finally {
            this.afterDispatch(request, response);
        }
    }

    protected void beforeDispatch(Request request, Response response) {
        this.requestContext.createContext();

        Cookie[] cookies = request.getCookies();
        this.app.instance(
                DispatcherServlet.class,
                this,
                "dispatcherServlet",
                ScopeType.REQUEST
            );
        this.app.instance(
                HttpServlet.class,
                this,
                "httpServlet",
                ScopeType.REQUEST
            );
        this.app.instance(Request.class, request, "request", ScopeType.REQUEST);
        this.app.instance(
                HttpServletRequest.class,
                request,
                "httpServletRequest",
                ScopeType.REQUEST
            );
        this.app.instance(
                Response.class,
                response,
                "response",
                ScopeType.REQUEST
            );
        this.app.instance(
                HttpServletResponse.class,
                response,
                "httpServletResponse",
                ScopeType.REQUEST
            );
        this.app.instance(
                CookieManager.class,
                new CookieManager(cookies),
                "cookieManager",
                ScopeType.REQUEST
            );
        this.app.instance(
                SessionManager.class,
                new SessionManager(request.getSession()),
                "sessionManager",
                ScopeType.REQUEST
            );
        this.app.instance(Auth.class, new Auth(), "auth", ScopeType.REQUEST);

        this.app.instance(
                WebContext.class,
                new WebContext(this.app),
                "webContext",
                ScopeType.REQUEST
            );
    }

    protected void doDispatch(Request request, Response response) {
        this.app.make(RouteManager.class).dispatch(request, response);
    }

    protected void afterDispatch(Request request, Response response) {
        this.requestContext.removeContext();
    }
}
