/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.server;

import java.util.EventListener;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * 服务器接口
 *
 * @author Otstar Lin
 * @date 2020/10/24 下午 4:52
 */
public interface Server {
    /**
     * 启动
     */
    void start();

    /**
     * 获取 Servlet 上下文
     *
     * @return Servlet 上下文
     */
    ServletContext getServletContext();

    /**
     * 添加过滤器
     *
     * @param name   过滤器名称
     * @param filter 过滤器
     *
     * @return Dynamic
     */
    default FilterRegistration.Dynamic addFilter(String name, Filter filter) {
        return this.getServletContext().addFilter(name, filter);
    }

    /**
     * 添加监听器
     *
     * @param listener 监听器
     */
    default void addListener(EventListener listener) {
        this.getServletContext().addListener(listener);
    }

    /**
     * 添加监听器（未启动服务器前）
     *
     * @param listener 监听器
     */
    void addListenerNotStart(EventListener listener);

    /**
     * 获取暂存的监听器列表
     *
     * @return 暂存的监听器列表
     */
    List<EventListener> getNotStartListenerList();

    /**
     * 添加 Servlet
     *
     * @param name    Servlet 名称
     * @param servlet Servlet
     *
     * @return Dynamic
     */
    default ServletRegistration.Dynamic addServlet(
        String name,
        Servlet servlet
    ) {
        return this.getServletContext().addServlet(name, servlet);
    }
}
