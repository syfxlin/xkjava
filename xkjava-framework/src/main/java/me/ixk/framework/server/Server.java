/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.server;

import java.util.EventListener;
import javax.servlet.ServletContext;

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
     * 停止
     */
    void stop();

    /**
     * 获取 Servlet 上下文
     *
     * @return Servlet 上下文
     */
    ServletContext getServletContext();

    /**
     * 添加过滤器
     *
     * @param spec 过滤器信息
     */
    void addFilter(FilterSpec spec);

    /**
     * 添加监听器
     *
     * @param listener 监听器
     */
    void addListener(EventListener listener);

    /**
     * 添加 Servlet
     *
     * @param spec Servlet 信息
     */
    void addServlet(ServletSpec spec);
}
