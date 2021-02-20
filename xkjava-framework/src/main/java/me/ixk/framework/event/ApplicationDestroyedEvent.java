package me.ixk.framework.event;

/**
 * 销毁后事件
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 10:20
 */
public class ApplicationDestroyedEvent extends ApplicationEvent {

    public ApplicationDestroyedEvent(Object source) {
        super(source);
    }
}
