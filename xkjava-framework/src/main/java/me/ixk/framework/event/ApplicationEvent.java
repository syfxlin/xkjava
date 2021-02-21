package me.ixk.framework.event;

/**
 * 事件
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 8:33
 */
public abstract class ApplicationEvent<T> {

    protected T source;
    private final long timestamp;

    public ApplicationEvent(final T source) {
        if (source == null) {
            throw new IllegalArgumentException("source is null");
        }
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    public T getSource() {
        return source;
    }

    public final long getTimestamp() {
        return timestamp;
    }
}
