/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.annotations.Servlet;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.context.RequestContext;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.ioc.context.SessionContext;
import me.ixk.framework.route.RouteManager;

/**
 * Servlet 调度器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:54
 */
@Servlet(url = "/*", name = { "dispatcherServlet" }, asyncSupported = true)
@MultipartConfig
public class DispatcherServlet extends AbstractFrameworkServlet {

    private static final long serialVersionUID = -5890247928905581053L;
    protected final XkJava app;
    protected final RequestContext requestContext;
    protected final SessionContext sessionContext;

    @Deprecated
    public DispatcherServlet(final XkJava app) {
        this.app = app;
        this.requestContext =
            (RequestContext) this.app.getContextByScope(ScopeType.REQUEST);
        this.sessionContext =
            (SessionContext) this.app.getContextByScope(ScopeType.SESSION);
    }

    @Override
    protected void dispatch(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) {
        Request request = new Request(req);
        Response response = new Response(resp);
        this.initContext(request, response);
        this.doDispatch(request, response);
        this.resetContext();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    protected void doDispatch(final Request request, final Response response) {
        this.app.make(RouteManager.class).dispatch(request, response);
    }

    public void initContext(Request request, Response response) {
        this.requestContext.setContext(request);
        this.sessionContext.setContext(request.getSession());
        this.app.setInstanceValue(Request.class, request);
        this.app.setInstanceValue(Response.class, response);
    }

    public void resetContext() {
        this.requestContext.removeContext();
        this.sessionContext.removeContext();
    }
}
