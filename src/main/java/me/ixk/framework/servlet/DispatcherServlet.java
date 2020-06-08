package me.ixk.framework.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.route.RouteManager;

public class DispatcherServlet extends HttpServlet {
    protected Application app;

    public DispatcherServlet() {
        super();
        this.app = Application.getInstance();
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
        this.app.instance(DispatcherServlet.class, this, "dispatcherServlet");
    }

    protected void doDispatch(Request request, Response response) {
        this.app.make(RouteManager.class).dispatch(request, response);
    }

    protected void afterDispatch(Request request, Response response) {
        this.app.remove(DispatcherServlet.class);
    }
}
