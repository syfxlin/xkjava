package me.ixk.framework.event;

import me.ixk.framework.ioc.XkJava;

/**
 * 销毁前事件
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 10:19
 */
public class ApplicationDestroyingEvent extends ApplicationContextEvent {

    public ApplicationDestroyingEvent(final XkJava source) {
        super(source);
    }
}
