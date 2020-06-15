package me.ixk.framework.servlet;

import static me.ixk.framework.ioc.RequestContext.currentAttributes;
import static me.ixk.framework.ioc.RequestContext.resetAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.factory.ObjectFactory;
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
        // 利用 ThreadLocal 实现线程安全
        RequestContext requestContext = RequestContext.create();
        requestContext.setDispatcherServlet(this);
        requestContext.setHttpServlet(this);
        requestContext.setRequest(request);
        requestContext.setHttpServletRequest(request);
        requestContext.setResponse(response);
        requestContext.setHttpServletResponse(response);
        Cookie[] cookies = request.getCookies();
        requestContext.setCookieManager(
            new CookieManager(cookies == null ? new Cookie[0] : cookies)
        );
        requestContext.setSessionManager(
            new SessionManager(request.getSession())
        );
        requestContext.setAuth(new Auth());

        this.app.instance(
                DispatcherServlet.class,
                (ObjectFactory<DispatcherServlet>) () ->
                    currentAttributes().getDispatcherServlet(),
                "dispatcherServlet"
            );
        this.app.instance(
                HttpServlet.class,
                (ObjectFactory<HttpServlet>) () ->
                    currentAttributes().getHttpServlet(),
                "httpServlet"
            );

        this.app.instance(
                Request.class,
                (ObjectFactory<Request>) () -> currentAttributes().getRequest(),
                "request"
            );
        this.app.instance(
                HttpServletRequest.class,
                (ObjectFactory<HttpServletRequest>) () ->
                    currentAttributes().getHttpServletRequest(),
                "httpServletRequest"
            );
        this.app.instance(
                Response.class,
                (ObjectFactory<Response>) () ->
                    currentAttributes().getResponse(),
                "response"
            );
        this.app.instance(
                HttpServletResponse.class,
                (ObjectFactory<HttpServletResponse>) () ->
                    currentAttributes().getHttpServletResponse(),
                "httpServletResponse"
            );
        this.app.instance(
                CookieManager.class,
                (ObjectFactory<CookieManager>) () ->
                    currentAttributes().getCookieManager()
            );
        this.app.instance(
                SessionManager.class,
                (ObjectFactory<SessionManager>) () ->
                    currentAttributes().getSessionManager()
            );
        this.app.instance(
                Auth.class,
                (ObjectFactory<Auth>) () -> currentAttributes().getAuth()
            );
    }

    protected void doDispatch(Request request, Response response) {
        this.app.make(RouteManager.class).dispatch(request, response);
    }

    protected void afterDispatch(Request request, Response response) {
        this.app.remove(DispatcherServlet.class);
        this.app.remove(HttpServlet.class);
        this.app.remove(Request.class);
        this.app.remove(HttpServletRequest.class);
        this.app.remove(Response.class);
        this.app.remove(HttpServletResponse.class);
        this.app.remove(CookieManager.class);
        this.app.remove(SessionManager.class);
        this.app.remove(Auth.class);
        resetAttributes();
    }
}
