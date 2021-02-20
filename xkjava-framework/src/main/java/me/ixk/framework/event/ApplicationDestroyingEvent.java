package me.ixk.framework.event;

/**
 * 销毁前事件
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 10:19
 */
public class ApplicationDestroyingEvent extends ApplicationEvent {

    public ApplicationDestroyingEvent(Object source) {
        super(source);
    }
}
