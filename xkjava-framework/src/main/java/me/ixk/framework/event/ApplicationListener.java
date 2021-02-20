package me.ixk.framework.event;

/**
 * 监听者
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 9:17
 */
@FunctionalInterface
public interface ApplicationListener {
    /**
     * 处理事件
     *
     * @param event 事件
     */
    void onEvent(ApplicationEvent event);
}
