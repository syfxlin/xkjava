/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.EventListener;
import java.util.stream.Collectors;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletSecurityElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebInitParam;
import me.ixk.framework.annotation.Filter;
import me.ixk.framework.annotation.Listener;
import me.ixk.framework.annotation.Servlet;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.server.FilterSpec;
import me.ixk.framework.server.Server;
import me.ixk.framework.server.ServletSpec;
import me.ixk.framework.util.MergedAnnotation;

/**
 * Servlet 组件加载器
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 8:22
 */
public class ServletComponentRegistry implements AfterBeanRegistry {

    @Override
    public void register(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        final Server server = app.make(Server.class);
        final Class<?> clazz = (Class<?>) element;
        if (
            annotation.hasAnnotation(Filter.class) &&
            javax.servlet.Filter.class.isAssignableFrom(clazz)
        ) {
            final Filter filter = annotation.getAnnotation(Filter.class);
            server.addFilter(
                new FilterSpec(
                    clazz.getName(),
                    filter.url(),
                    (javax.servlet.Filter) app.make(clazz),
                    filter.dispatcherTypes(),
                    Arrays
                        .stream(filter.initParams())
                        .collect(
                            Collectors.toMap(
                                WebInitParam::name,
                                WebInitParam::value
                            )
                        ),
                    filter.asyncSupported()
                )
            );
        }
        if (
            annotation.hasAnnotation(Listener.class) &&
            EventListener.class.isAssignableFrom(clazz)
        ) {
            server.addListener((EventListener) app.make(clazz));
        }
        if (
            annotation.hasAnnotation(Servlet.class) &&
            javax.servlet.Servlet.class.isAssignableFrom(clazz)
        ) {
            final Servlet servlet = annotation.getAnnotation(Servlet.class);
            final MultipartConfig multipartConfig = annotation.getAnnotation(
                MultipartConfig.class
            );
            final ServletSecurity servletSecurity = annotation.getAnnotation(
                ServletSecurity.class
            );
            server.addServlet(
                new ServletSpec(
                    clazz.getName(),
                    servlet.url(),
                    (javax.servlet.Servlet) app.make(clazz),
                    servlet.loadOnStartup(),
                    Arrays
                        .stream(servlet.initParams())
                        .collect(
                            Collectors.toMap(
                                WebInitParam::name,
                                WebInitParam::value
                            )
                        ),
                    servlet.asyncSupported(),
                    multipartConfig != null
                        ? new MultipartConfigElement(multipartConfig)
                        : null,
                    servletSecurity != null
                        ? new ServletSecurityElement(servletSecurity)
                        : null
                )
            );
        }
    }
}
