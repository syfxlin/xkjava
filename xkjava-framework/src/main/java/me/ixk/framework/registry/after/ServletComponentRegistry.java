/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.EventListener;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebInitParam;
import me.ixk.framework.annotations.Filter;
import me.ixk.framework.annotations.Listener;
import me.ixk.framework.annotations.Servlet;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.server.Server;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * Servlet 组件加载器
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 8:22
 */
public class ServletComponentRegistry implements AfterImportBeanRegistry {

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
            final Dynamic registration = server.addFilter(
                clazz.getName(),
                (javax.servlet.Filter) app.make(clazz)
            );
            final EnumSet<DispatcherType> dispatcherSet = EnumSet.noneOf(
                DispatcherType.class
            );
            dispatcherSet.addAll(Arrays.asList(filter.dispatcherTypes()));
            registration.addMappingForUrlPatterns(
                dispatcherSet,
                true,
                filter.url()
            );
            registration.setAsyncSupported(filter.asyncSupported());
            for (final WebInitParam param : filter.initParams()) {
                registration.setInitParameter(param.name(), param.value());
            }
        }
        if (
            annotation.hasAnnotation(Listener.class) &&
            EventListener.class.isAssignableFrom(clazz)
        ) {
            // 在 ServletContext 未启动之前无法添加监听器，所以要先缓存下来，然后通过监听 ServletContext 启动，把监听器注入进去
            server.addListenerNotStart((EventListener) app.make(clazz));
        }
        if (
            annotation.hasAnnotation(Servlet.class) &&
            javax.servlet.Servlet.class.isAssignableFrom(clazz)
        ) {
            final Servlet servlet = annotation.getAnnotation(Servlet.class);
            final ServletRegistration.Dynamic registration = server.addServlet(
                clazz.getName(),
                (javax.servlet.Servlet) app.make(clazz)
            );
            registration.addMapping(servlet.url());
            registration.setAsyncSupported(servlet.asyncSupported());
            registration.setLoadOnStartup(servlet.loadOnStartup());
            for (final WebInitParam param : servlet.initParams()) {
                registration.setInitParameter(param.name(), param.value());
            }
            final MultipartConfig multipartConfig = annotation.getAnnotation(
                MultipartConfig.class
            );
            if (multipartConfig != null) {
                registration.setMultipartConfig(
                    new MultipartConfigElement(multipartConfig)
                );
            }
            final ServletSecurity servletSecurity = annotation.getAnnotation(
                ServletSecurity.class
            );
            if (servletSecurity != null) {
                registration.setServletSecurity(
                    new ServletSecurityElement(servletSecurity)
                );
            }
        }
    }
}
