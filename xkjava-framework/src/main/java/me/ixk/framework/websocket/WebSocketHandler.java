package me.ixk.framework.websocket;

/**
 * WebSocket 处理器
 *
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:14
 */
public interface WebSocketHandler {
    /**
     * 连接后处理器
     *
     * @param session WebSocketSession
     * @throws Exception 异常
     */
    default void handleConnected(WebSocketSession session) throws Exception {}

    /**
     * 处理信息
     *
     * @param session WebSocketSession
     * @param message 信息
     * @throws Exception 异常
     */
    default void handleMessage(
        WebSocketSession session,
        WebSocketMessage<?> message
    ) throws Exception {
        if (message instanceof TextMessage) {
            handleTextMessage(session, (TextMessage) message);
        } else if (message instanceof BinaryMessage) {
            handleBinaryMessage(session, (BinaryMessage) message);
        } else if (message instanceof PongMessage) {
            handlePongMessage(session, (PongMessage) message);
        } else if (message instanceof PingMessage) {
            handlePingMessage(session, (PingMessage) message);
        } else {
            throw new IllegalStateException(
                "Unexpected WebSocket message type: " + message
            );
        }
    }

    /**
     * 处理文字信息
     *
     * @param session WebSocketSession
     * @param message 信息
     * @throws Exception 异常
     */
    default void handleTextMessage(
        WebSocketSession session,
        TextMessage message
    ) throws Exception {}

    /**
     * 处理二进制信息
     *
     * @param session WebSocketSession
     * @param message 信息
     * @throws Exception 异常
     */
    default void handleBinaryMessage(
        WebSocketSession session,
        BinaryMessage message
    ) throws Exception {}

    /**
     * 处理 Pong 信息
     *
     * @param session WebSocketSession
     * @param message 信息
     * @throws Exception 异常
     */
    default void handlePongMessage(
        WebSocketSession session,
        PongMessage message
    ) throws Exception {}

    /**
     * 处理 Ping 信息
     *
     * @param session WebSocketSession
     * @param message 信息
     * @throws Exception 异常
     */
    default void handlePingMessage(
        WebSocketSession session,
        PingMessage message
    ) throws Exception {}

    /**
     * 处理错误
     *
     * @param session WebSocketSession
     * @param cause   异常
     * @throws Exception 异常
     */
    default void handleError(WebSocketSession session, Throwable cause)
        throws Exception {
        if (cause instanceof Exception) {
            throw (Exception) cause;
        } else {
            throw new Exception(cause);
        }
    }

    /**
     * 关闭后处理器
     *
     * @param session     WebSocketSession
     * @param closeStatus 关闭状态
     * @throws Exception 异常
     */
    default void handleClosed(
        WebSocketSession session,
        CloseStatus closeStatus
    ) throws Exception {}
}
