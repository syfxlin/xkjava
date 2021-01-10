package me.ixk.framework.ioc.factory;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

/**
 * 实例提供者
 *
 * @author Otstar Lin
 * @date 2020/12/29 下午 7:30
 */
public interface ObjectProvider<T> extends ObjectFactory<T>, Iterable<T> {
    /**
     * 获取集合对象
     *
     * @return 集合
     */
    Collection<T> getObjects();

    /**
     * 获取对象实例，如果不存在则返回 Supplier 的返回值
     *
     * @param defaultSupplier 生产者
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

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    @NotNull
    default Iterator<T> iterator() {
        return getObjects().iterator();
    }
}
