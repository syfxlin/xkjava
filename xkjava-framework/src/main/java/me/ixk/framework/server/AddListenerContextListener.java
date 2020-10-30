/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.server;

import java.util.EventListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import me.ixk.framework.ioc.XkJava;

/**
 * 添加监听器
 * <p>
 * 在 ServletContext 未启动之前无法添加监听器，所以要先缓存下来，然后通过监听 ServletContext 启动，把监听器注入进去
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 9:41
 */
@WebListener
public class AddListenerContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final Server server = XkJava.of().make(Server.class);
        for (final EventListener listener : server.getNotStartListenerList()) {
            server.addListener(listener);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {}
}
