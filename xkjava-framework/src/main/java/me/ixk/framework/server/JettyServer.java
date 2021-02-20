/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.server;

import java.util.EventListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.ServletContext;
import me.ixk.framework.annotation.Component;
import me.ixk.framework.config.AppProperties;
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
@Component(
    name = { " jetty ", "server" },
    type = me.ixk.framework.server.Server.class
)
public class JettyServer implements me.ixk.framework.server.Server {

    private Server server;
    private WebAppContext context;
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();

    public JettyServer(AppProperties properties) {
        Resource resource = Resource.newClassPathResource("/public");
        this.buildServer(properties.getPort(), resource);
    }

    @Override
    public void start() {
        this.startServer();
    }

    @Override
    public ServletContext getServletContext() {
        return this.context.getServletContext();
    }

    @Override
    public void addListenerNotStart(EventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public List<EventListener> getNotStartListenerList() {
        return this.listeners;
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

    private void buildServer(int port, Resource resource) {
        this.server = new Server();
        ServerConnector connector = new ServerConnector(this.server);
        connector.setPort(port);
        this.server.addConnector(connector);

        this.context = new WebAppContext();
        this.context.setContextPath("/");
        this.context.setBaseResource(Resource.newClassPathResource(""));
        this.context.setParentLoaderPriority(true);

        this.context.setConfigurations(
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
        this.context.setAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/classes/.*"
            );

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(resource);

        HandlerList handlerList = new HandlerList(
            resourceHandler,
            this.context
        );

        this.server.setHandler(handlerList);
    }
}
