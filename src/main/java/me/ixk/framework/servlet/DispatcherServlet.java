package me.ixk.framework.servlet;

import static me.ixk.framework.ioc.RequestContext.*;

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
            // 确保请求出错时能清空请求周期注入的实例
            this.afterDispatch(request, response);
        }
    }

    protected void beforeDispatch(Request request, Response response) {
        // 利用 ThreadLocal 实现线程安全
        RequestContext requestContext = new RequestContext();
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
            new SessionManager(
                request.getSession(),
                request.getSessionManager()
            )
        );
        requestContext.setAuth(new Auth());
        setRequestAttributes(requestContext);

        this.app.instance(
                DispatcherServlet.class,
                (ObjectFactory<DispatcherServlet>) () ->
                    currentRequestAttributes()
                        .getObject(DispatcherServlet.class),
                "dispatcherServlet"
            );
        this.app.instance(
                HttpServlet.class,
                (ObjectFactory<HttpServlet>) () ->
                    currentRequestAttributes().getObject(HttpServlet.class),
                "httpServlet"
            );

        this.app.instance(
                Request.class,
                (ObjectFactory<Request>) () ->
                    currentRequestAttributes().getObject(Request.class),
                "request"
            );
        this.app.instance(
                HttpServletRequest.class,
                (ObjectFactory<HttpServletRequest>) () ->
                    currentRequestAttributes()
                        .getObject(HttpServletRequest.class),
                "httpServletRequest"
            );
        this.app.instance(
                Response.class,
                (ObjectFactory<Response>) () ->
                    currentRequestAttributes().getObject(Response.class),
                "response"
            );
        this.app.instance(
                HttpServletResponse.class,
                (ObjectFactory<HttpServletResponse>) () ->
                    currentRequestAttributes()
                        .getObject(HttpServletResponse.class),
                "httpServletResponse"
            );
        this.app.instance(
                CookieManager.class,
                (ObjectFactory<CookieManager>) () ->
                    currentRequestAttributes().getObject(CookieManager.class)
            );
        this.app.instance(
                SessionManager.class,
                (ObjectFactory<SessionManager>) () ->
                    currentRequestAttributes().getObject(SessionManager.class)
            );
        this.app.instance(
                Auth.class,
                (ObjectFactory<Auth>) () ->
                    currentRequestAttributes().getObject(Auth.class)
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
        this.app.make(CookieManager.class);
        this.app.make(SessionManager.class);
        this.app.make(Auth.class);
        resetRequestAttributes();
    }
}
