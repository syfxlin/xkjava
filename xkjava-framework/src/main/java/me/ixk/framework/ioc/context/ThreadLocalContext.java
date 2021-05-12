package me.ixk.framework.ioc.context;

/**
 * @author Otstar Lin
 * @date 2021/1/13 下午 4:23
 */
public interface ThreadLocalContext<T> extends Context {
    /**
     * 删除 Context
     */
    void removeContext();

    /**
     * 获取 Context
     *
     * @return Request 对象
     */
    T getContext();

    /**
     * 设置 Context
     *
     * @param context Request 对象
     */
    void setContext(T context);

    /**
     * 该 Context 是否启动，一般的 Context 只要 new 后就会启动 但是如果是 ThreadLocal 则需要另行启动
     *
     * @return 是否启动
     */
    @Override
    boolean isCreated();

    /**
     * 是否需要代理
     *
     * @return 是否需要代理
     */
    @Override
    default boolean useProxy() {
        return true;
    }
}
