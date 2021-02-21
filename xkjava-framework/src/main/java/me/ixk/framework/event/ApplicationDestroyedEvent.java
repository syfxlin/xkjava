package me.ixk.framework.event;

import me.ixk.framework.ioc.XkJava;

/**
 * 销毁后事件
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 10:20
 */
public class ApplicationDestroyedEvent extends ApplicationContextEvent {

    public ApplicationDestroyedEvent(final XkJava source) {
        super(source);
    }
}
