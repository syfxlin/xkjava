package me.ixk.framework.event;

import me.ixk.framework.ioc.XkJava;

/**
 * 启动前事件
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 10:17
 */
public class ApplicationBootingEvent extends ApplicationContextEvent {

    public ApplicationBootingEvent(final XkJava source) {
        super(source);
    }
}
