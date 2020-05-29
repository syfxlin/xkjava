package me.ixk.framework.servlet;

import me.ixk.app.middleware.Handler1;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.route.RouteDispatcher;
import me.ixk.framework.route.RouteResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DispatcherServlet extends HttpServlet {

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
        RouteDispatcher dispatcher = RouteDispatcher.dispatcher(
            r -> {
                r.addGroup(
                    "/user",
                    rr -> {
                        rr.addRoute("GET", "", new Handler1());
                        rr.addRoute("GET", "/{id: \\d+}", new Handler1());
                        rr.addRoute(
                            "GET",
                            "/{id: \\d+}/{name}",
                            new Handler1()
                        );
                    }
                );
            }
        );
        RouteResult result = dispatcher.dispatch(
            request.getMethod(),
            request.getUri().getPath()
        );
        System.out.println(
            ((RouteResult) result.getHandler().handle(result)).getRoute()
        );
    }
}
