/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.server;

import cn.hutool.core.thread.ThreadUtil;
import java.util.EventListener;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.ServletSecurityElement;
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.annotation.core.PreDestroy;
import me.ixk.framework.config.AppProperties;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ListenerHolder;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.Source;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jetty 服务器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:53
 */
@Component(name = { "jetty", "server" })
public class JettyServer implements me.ixk.framework.server.Server {

    private static final Logger log = LoggerFactory.getLogger(
        JettyServer.class
    );
    private Server server;
    private WebAppContext context;

    public JettyServer(final AppProperties properties) {
        final Resource resource = Resource.newClassPathResource("/public");
        this.buildServer(properties.getPort(), resource);
    }

    @Override
    public synchronized void start() {
        ThreadUtil
            .newThread(
                () -> {
                    try {
                        this.server.start();
                    } catch (final Exception e) {
                        log.error("Jetty start failed", e);
                    }
                    try {
                        this.server.join();
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.info("Jetty interrupt", e);
                    }
                },
                "jetty"
            )
            .start();
    }

    @Override
    @PreDestroy
    public synchronized void stop() {
        try {
            this.server.stop();
        } catch (Exception e) {
            log.error("Jetty stop error", e);
        }
    }

    @Override
    public ServletContext getServletContext() {
        return this.context.getServletContext();
    }

    @Override
    public void addFilter(final FilterSpec spec) {
        final ServletHandler servletHandler = this.context.getServletHandler();
        final FilterHolder holder = servletHandler.newFilterHolder(
            Source.EMBEDDED
        );
        holder.setName(spec.getName());
        holder.setFilter(spec.getFilter());
        holder.setInitParameters(spec.getInitParams());
        holder.setAsyncSupported(spec.isAsyncSupported());
        final FilterMapping mapping = new FilterMapping();
        mapping.setDispatcherTypes(spec.getDispatcherTypes());
        mapping.setFilterName(spec.getName());
        mapping.setPathSpecs(spec.getUrl());
        servletHandler.addFilter(holder, mapping);
    }

    @Override
    public void addListener(final EventListener listener) {
        final ServletHandler servletHandler = this.context.getServletHandler();
        final ListenerHolder holder = servletHandler.newListenerHolder(
            Source.EMBEDDED
        );
        holder.setListener(listener);
        servletHandler.addListener(holder);
    }

    @Override
    public void addServlet(final ServletSpec spec) {
        final ServletHandler servletHandler = this.context.getServletHandler();
        final ServletHolder holder = servletHandler.newServletHolder(
            Source.EMBEDDED
        );
        holder.setName(spec.getName());
        holder.setServlet(spec.getServlet());
        holder.setInitParameters(spec.getInitParams());
        holder.setAsyncSupported(spec.isAsyncSupported());
        final Dynamic registration = holder.getRegistration();
        registration.setLoadOnStartup(spec.getLoadOnStartup());
        final MultipartConfigElement multipartConfig = spec.getMultipartConfig();
        if (multipartConfig != null) {
            registration.setMultipartConfig(multipartConfig);
        }
        final ServletSecurityElement servletSecurity = spec.getServletSecurity();
        if (servletSecurity != null) {
            registration.setServletSecurity(servletSecurity);
        }
        servletHandler.addServlet(holder);
        registration.addMapping(spec.getUrl());
    }

    public Server server() {
        return this.server;
    }

    private void buildServer(final int port, final Resource resource) {
        this.server = new Server();
        final ServerConnector connector = new ServerConnector(this.server);
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

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(resource);

        final HandlerList handlerList = new HandlerList(
            resourceHandler,
            this.context
        );

        this.server.setHandler(handlerList);
    }
}
