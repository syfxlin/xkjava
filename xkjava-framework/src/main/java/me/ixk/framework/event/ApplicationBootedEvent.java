package me.ixk.framework.event;

/**
 * 启动完成事件
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 10:18
 */
public class ApplicationBootedEvent extends ApplicationEvent {

    public ApplicationBootedEvent(final Object source) {
        super(source);
    }
}
