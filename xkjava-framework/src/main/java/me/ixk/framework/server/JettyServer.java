/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.server;

import me.ixk.framework.annotations.Component;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.ErrorHandler;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

/**
 * Jetty 服务器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:53
 */
@Component(name = { " jetty ", "server", "me.ixk.framework.server.Server" })
public class JettyServer implements me.ixk.framework.server.Server {
    private final XkJava app;
    private Server server;

    public JettyServer(XkJava app) {
        this.app = app;
    }

    @Override
    public void start() {
        Resource resource = Resource.newClassPathResource("/public");
        int port = this.app.env().get("app.port", 8080);
        this.server = this.buildServer(port, resource);
        this.startServer();
    }

    public Server server() {
        return this.server;
    }

    private void startServer() {
        Thread thread = new Thread(
            () -> {
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
        );
        thread.setName("jetty");
        thread.start();
    }

    private Server buildServer(int port, Resource resource) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setBaseResource(Resource.newClassPathResource(""));
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

        ErrorHandler errorHandler = new ErrorHandler();
        context.setErrorHandler(errorHandler);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(resource);

        HandlerList handlerList = new HandlerList(resourceHandler, context);

        server.setHandler(handlerList);

        return server;
    }
}
