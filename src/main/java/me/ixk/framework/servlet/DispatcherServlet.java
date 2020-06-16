package me.ixk.framework.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.http.CookieManager;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SessionManager;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.ioc.RequestContext;
import me.ixk.framework.kernel.Auth;
import me.ixk.framework.route.RouteManager;

public class DispatcherServlet extends FrameworkServlet {
    protected final Application app;

    @Deprecated
    public DispatcherServlet() {
        super();
        this.app = Application.get();
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
        this.app.createRequestContext();

        Cookie[] cookies = request.getCookies();
        RequestContext context = this.app.getRequestContext();
        context.setObject(DispatcherServlet.class, this);
        context.setObject(HttpServlet.class, this);
        context.setObject(Request.class, request);
        context.setObject(HttpServletRequest.class, request);
        context.setObject(Response.class, response);
        context.setObject(HttpServletResponse.class, response);
        context.setObject(CookieManager.class, new CookieManager(cookies));
        context.setObject(
            SessionManager.class,
            new SessionManager(request.getSession())
        );
        context.setObject(Auth.class, new Auth());
    }

    protected void doDispatch(Request request, Response response) {
        this.app.make(RouteManager.class).dispatch(request, response);
    }

    protected void afterDispatch(Request request, Response response) {
        this.app.removeRequestContext();
    }
}
