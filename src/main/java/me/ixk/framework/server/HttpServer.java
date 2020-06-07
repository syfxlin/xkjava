package me.ixk.framework.server;

import java.io.File;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.route.RouteManager;
import me.ixk.framework.servlet.DispatcherServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class HttpServer {

    public static void main(String[] args) {
        String rootDir = System.getProperty("user.dir");
        String resource = rootDir + File.separator + "public";
        int port = 8090;

        Server server = buildServer(port, resource);

        startApplication();

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void startApplication() {
        Application application = Application.createAndBoot();
        application.instance(
            RouteManager.class,
            new RouteManager(),
            "routeManager"
        );
    }

    public static Server buildServer(int port, String resource) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase(resource);

        //        ServletHandler servletHandler = new ServletHandler();
        //        servletHandler.addServletWithMapping(DispatcherServlet.class, "/*");

        //        SessionHandler sessionHandler = new SessionHandler();

        ServletContextHandler servletContextHandler = new ServletContextHandler(
            ServletContextHandler.SESSIONS
        );
        servletContextHandler.setContextPath("/");
        servletContextHandler.addServlet(DispatcherServlet.class, "/*");
        servletContextHandler.setResourceBase(resource + "/..");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(
            new Handler[] {
                resourceHandler,
                //                sessionHandler,
                //                servletHandler,
                servletContextHandler,
            }
        );
        server.setHandler(handlers);
        server.setSessionIdManager(new HashSessionIdManager());

        return server;
    }
}
