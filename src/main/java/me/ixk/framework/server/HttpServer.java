package me.ixk.framework.server;

import java.io.File;
import me.ixk.framework.facades.Config;
import me.ixk.framework.kernel.ErrorHandler;
import me.ixk.framework.servlet.DispatcherServlet;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.*;

public class HttpServer {
    private final Server server;

    private HttpServer() {
        String rootDir = System.getProperty("user.dir");
        String resource = rootDir + File.separator + "public";
        this.server =
            this.buildServer(Config.get("app.port", Integer.class), resource);
    }

    public static HttpServer create() {
        return new HttpServer();
    }

    public void start() {
        try {
            this.server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Server getJettyServer() {
        return server;
    }

    private Server buildServer(int port, String resource) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setResourceBase(resource);
        context.setParentLoaderPriority(true);

        context.setConfigurations(
            new Configuration[] {
                new AnnotationConfiguration(),
                new WebInfConfiguration(),
                new WebXmlConfiguration(),
                new MetaInfConfiguration(),
                new FragmentConfiguration(),
                new EnvConfiguration(),
                new PlusConfiguration(),
                new JettyWebXmlConfiguration(),
            }
        );
        // 配置 Jetty Embedded 注解扫描包含的路径
        context.setAttribute(
            "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
            ".*/classes/.*"
        );
        context.addServlet(DispatcherServlet.class, "/*");

        ErrorHandler errorHandler = new ErrorHandler();
        context.setErrorHandler(errorHandler);

        server.setHandler(context);

        return server;
    }
}
