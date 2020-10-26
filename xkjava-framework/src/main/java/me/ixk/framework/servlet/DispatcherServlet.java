/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.context.RequestContext;
import me.ixk.framework.ioc.context.SessionContext;
import me.ixk.framework.route.RouteManager;

/**
 * Servlet 调度器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:54
 */
@WebServlet(urlPatterns = "/*")
@MultipartConfig
@Component(name = { "dispatcherServlet", "javax.servlet.http.HttpServlet" })
@Scope(type = ScopeType.REQUEST)
public class DispatcherServlet extends AbstractFrameworkServlet {

    private static final long serialVersionUID = -5890247928905581053L;
    protected final XkJava app;
    protected final RequestContext requestContext;
    protected final SessionContext sessionContext;

    @Deprecated
    public DispatcherServlet() {
        super();
        this.app = XkJava.of();
        this.requestContext = (RequestContext) this.app
            .getContextByScope(ScopeType.REQUEST);
        this.sessionContext = (SessionContext) this.app
            .getContextByScope(ScopeType.SESSION);
    }

    @Override
    protected void dispatch(final Request request, final Response response) {
        try {
            this.beforeDispatch(request, response);
            this.doDispatch(request, response);
        } finally {
            this.afterDispatch(request, response);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    protected void beforeDispatch(final Request request,
        final Response response) {
        this.requestContext.setContext(request);
        this.sessionContext.setContext(request.getSession());
        this.app.setInstanceValue(DispatcherServlet.class, this);
        this.app.setInstanceValue(Request.class, request);
        this.app.setInstanceValue(Response.class, response);
    }

    protected void doDispatch(final Request request, final Response response) {
        this.app.make(RouteManager.class).dispatch(request, response);
    }

    protected void afterDispatch(final Request request,
        final Response response) {
        this.requestContext.removeContext();
        this.sessionContext.removeContext();
    }
}
