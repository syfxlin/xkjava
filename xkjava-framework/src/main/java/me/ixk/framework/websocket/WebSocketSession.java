package me.ixk.framework.websocket;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * WebSocketSession
 *
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:15
 */
public interface WebSocketSession extends Closeable {
    /**
     * 关闭
     *
     * @throws IOException 异常
     */
    @Override
    default void close() throws IOException {
        this.close(CloseStatus.NORMAL);
    }

    /**
     * 关闭
     *
     * @param code   响应码
     * @param reason 描述
     * @throws IOException 异常
     */
    default void close(int code, String reason) throws IOException {
        this.close(new CloseStatus(code, reason));
    }

    /**
     * 关闭
     *
     * @param closeStatus 关闭状态
     * @throws IOException 异常
     */
    void close(CloseStatus closeStatus) throws IOException;

    /**
     * 是否开启
     *
     * @return 是否开启
     */
    boolean isOpen();

    /**
     * 发送信息
     *
     * @param message 信息
     * @throws IOException 异常
     */
    default void send(WebSocketMessage<?> message) throws IOException {
        if (message instanceof TextMessage) {
            sendText((TextMessage) message);
        } else if (message instanceof BinaryMessage) {
            sendBinary((BinaryMessage) message);
        } else if (message instanceof PingMessage) {
            sendPing((PingMessage) message);
        } else if (message instanceof PongMessage) {
            sendPong((PongMessage) message);
        } else {
            throw new IllegalStateException(
                "Unexpected WebSocketMessage type: " + message
            );
        }
    }

    /**
     * 发送信息
     *
     * @param message 信息
     * @throws IOException 异常
     */
    void sendText(TextMessage message) throws IOException;

    /**
     * 发送信息
     *
     * @param message 信息
     * @throws IOException 异常
     */
    void sendBinary(BinaryMessage message) throws IOException;

    /**
     * 发送信息
     *
     * @param message 信息
     * @throws IOException 异常
     */
    void sendPing(PingMessage message) throws IOException;

    /**
     * 发送信息
     *
     * @param message 信息
     * @throws IOException 异常
     */
    void sendPong(PongMessage message) throws IOException;

    /**
     * 获取本地 Socket 地址
     *
     * @return Socket 地址
     */
    InetSocketAddress getLocalAddress();

    /**
     * 获取远程 Socket 地址
     *
     * @return Socket 地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * 获取头字段
     *
     * @return 头字段
     */
    Map<String, List<String>> getHeaders();

    /**
     * 获取协议版本
     *
     * @return 协议版本
     */
    String getProtocolVersion();

    /**
     * 确定协商的扩展名
     *
     * @return 扩展名
     */
    List<WebSocketExtension> getExtensions();
}
