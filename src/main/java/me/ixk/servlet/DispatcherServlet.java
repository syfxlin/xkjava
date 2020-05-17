package me.ixk.servlet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.http.Request;
import me.ixk.http.Response;
import me.ixk.middleware.*;

public class DispatcherServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
    }

    /**
     * HttpServer Start -> Load Shared Config -> DispatcherServlet -> Load Single Config
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
        Queue<MiddlewareInterface> queue = new LinkedList<>();
        queue.add(new Middleware1());
        queue.add(new Middleware2());
        HandlerInterface handler = new Handler1();
        Runner runner = new Runner(handler, queue);
        response.content(
            runner
                .then(request.input("hoverEvent.action.1").getAsString())
                .toString()
        );
    }
}
