package me.ixk.framework.event;

import me.ixk.framework.ioc.XkJava;

/**
 * @author Otstar Lin
 * @date 2021/2/21 下午 2:56
 */
public abstract class ApplicationContextEvent extends ApplicationEvent<XkJava> {

    public ApplicationContextEvent(XkJava source) {
        super(source);
    }
}
