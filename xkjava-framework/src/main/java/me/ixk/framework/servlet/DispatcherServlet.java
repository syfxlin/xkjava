/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import me.ixk.framework.annotations.Servlet;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.route.RouteManager;

/**
 * Servlet 调度器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:54
 */
@Servlet(
    url = "/*",
    name = { "dispatcherServlet", "javax.servlet.http.HttpServlet" }
)
@MultipartConfig
public class DispatcherServlet extends AbstractFrameworkServlet {
    private static final long serialVersionUID = -5890247928905581053L;
    protected final XkJava app;

    @Deprecated
    public DispatcherServlet(XkJava app) {
        this.app = app;
    }

    @Override
    protected void dispatch(final Request request, final Response response) {
        this.doDispatch(request, response);
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
}
