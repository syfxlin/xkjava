package me.ixk.framework.ioc;

/**
 * 在 Bean 销毁前执行，instance 绑定的 Bean 无法被处理
 */
public interface BeanAfterProcessor {
    Object process(Object instance, Binding binding);
}
