package me.ixk.framework.servlet;

import java.io.IOException;
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
import me.ixk.framework.kernel.Auth;
import me.ixk.framework.route.RouteManager;

public class DispatcherServlet extends HttpServlet {
    protected Application app;

    @Deprecated
    public DispatcherServlet() {
        super();
        this.app = Application.get();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    /**
     * HttpServer Start -> Load Shared Environment -> DispatcherServlet -> Load Single Environment
     *
     * DispatcherServlet -> Middleware -> Handler => Controller@Method
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        Request request = new Request((org.eclipse.jetty.server.Request) req);
        Response response = new Response(
            (org.eclipse.jetty.server.Response) resp
        );
        this.dispatch(request, response);
    }

    protected void dispatch(Request request, Response response) {
        this.beforeDispatch(request, response);
        this.doDispatch(request, response);
        this.afterDispatch(request, response);
    }

    protected void beforeDispatch(Request request, Response response) {
        this.app.instance(
                DispatcherServlet.class,
                (ObjectFactory<DispatcherServlet>) () -> this,
                "dispatcherServlet"
            );
        this.app.instance(
                HttpServlet.class,
                (ObjectFactory<DispatcherServlet>) () -> this,
                "httpServlet"
            );

        this.app.instance(
                Request.class,
                (ObjectFactory<Request>) () -> request,
                "request"
            );
        this.app.instance(
                HttpServletRequest.class,
                (ObjectFactory<HttpServletRequest>) () -> request,
                "httpServletRequest"
            );
        this.app.instance(
                Response.class,
                (ObjectFactory<Response>) () -> response,
                "response"
            );
        this.app.instance(
                HttpServletResponse.class,
                (ObjectFactory<HttpServletResponse>) () -> response,
                "httpServletResponse"
            );
        Cookie[] cookies = request.getCookies();
        this.app.make(CookieManager.class)
            .refresh(cookies == null ? new Cookie[0] : cookies);
        this.app.make(SessionManager.class)
            .refresh(request.getSession(), request.getSessionManager());
        this.app.make(Auth.class).refresh();
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
        this.app.make(CookieManager.class).refresh(new Cookie[0]);
        this.app.make(SessionManager.class).refresh(null, null);
        this.app.make(Auth.class).refresh();
    }
}
