package me.ixk.framework.event;

/**
 * 启动前事件
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 10:17
 */
public class ApplicationBootingEvent extends ApplicationEvent {

    public ApplicationBootingEvent(final Object source) {
        super(source);
    }
}
