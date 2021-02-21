package me.ixk.framework.event;

import me.ixk.framework.ioc.XkJava;

/**
 * 启动完成事件
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 10:18
 */
public class ApplicationBootedEvent extends ApplicationContextEvent {

    public ApplicationBootedEvent(final XkJava source) {
        super(source);
    }
}
