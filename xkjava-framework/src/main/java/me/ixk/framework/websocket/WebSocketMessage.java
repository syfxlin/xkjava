package me.ixk.framework.websocket;

import java.util.Objects;

/**
 * WebSocket 信息
 *
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:02
 */
public abstract class WebSocketMessage<T> {

    private final T payload;

    public WebSocketMessage(final T payload) {
        this.payload = payload;
    }

    /**
     * 负载
     *
     * @return 负载
     */
    public T payload() {
        return this.payload;
    }

    /**
     * 长度
     *
     * @return 长度
     */
    abstract int length();

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WebSocketMessage<?> that = (WebSocketMessage<?>) o;
        return Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payload);
    }

    @Override
    public String toString() {
        return (
            this.getClass().getSimpleName() +
            "{payload=" +
            payload +
            ", length=" +
            this.length() +
            "}"
        );
    }
}
