package me.ixk.app.middleware;

import java.io.IOException;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.middleware.Handler;
import me.ixk.framework.servlet.DispatcherServlet;
import me.ixk.framework.utils.Thymeleaf;
import org.thymeleaf.context.WebContext;

public class Handler1 implements Handler {

    @Override
    public Object handle(Request request, Response response) {
        WebContext webContext = new WebContext(
            request.getOriginRequest(),
            response.getOriginResponse(),
            Application
                .getInstance()
                .make(DispatcherServlet.class)
                .getServletContext()
        );
        try {
            return response.content(
                Application
                    .getInstance()
                    .make(Thymeleaf.class)
                    .getTemplateEngine()
                    .process("index", webContext)
            );
        } catch (IOException e) {
            return null;
        }
    }
}
