package me.ixk.framework.ioc.factory;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 实例提供者
 *
 * @author Otstar Lin
 * @date 2020/12/29 下午 7:30
 */
public interface ObjectProvider<T> extends ObjectFactory<T> {
    /**
     * 获取对象实例，显示构造
     *
     * @param args 参数
     *
     * @return 对象
     */
    T getObject(Map<String, Object> args);

    /**
     * 获取对象实例，如果不存在则返回 Supplier 的返回值
     *
     * @param defaultSupplier 生产者
     *
     * @return 对象实例
     */
    default T getIfAvailable(Supplier<T> defaultSupplier) {
        T dependency = getObject();
        return (dependency != null ? dependency : defaultSupplier.get());
    }

    /**
     * 获取对象实例，如果存在则执行 Consumer
     *
     * @param dependencyConsumer 消费者
     */
    default void ifAvailable(Consumer<T> dependencyConsumer) {
        T dependency = getObject();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }
    }
}
