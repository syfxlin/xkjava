package me.ixk.framework.test.event;

import me.ixk.framework.event.ApplicationContextEvent;
import me.ixk.framework.ioc.XkJava;

/**
 * @author Otstar Lin
 * @date 2021/2/21 下午 2:54
 */
public class AfterTestEachEvent extends ApplicationContextEvent {

    public AfterTestEachEvent(final XkJava source) {
        super(source);
    }
}
