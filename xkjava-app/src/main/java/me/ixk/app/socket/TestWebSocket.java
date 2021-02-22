package me.ixk.app.socket;

import me.ixk.framework.annotation.web.WebSocket;
import me.ixk.framework.websocket.BinaryMessage;
import me.ixk.framework.websocket.PingMessage;
import me.ixk.framework.websocket.PongMessage;
import me.ixk.framework.websocket.TextMessage;
import me.ixk.framework.websocket.WebSocketHandler;
import me.ixk.framework.websocket.WebSocketSession;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 5:24
 */
@WebSocket("/socket")
public class TestWebSocket implements WebSocketHandler {

    @Override
    public void handleTextMessage(
        final WebSocketSession session,
        final TextMessage message
    ) throws Exception {
        System.out.println(message);
        session.sendText(new TextMessage("response: " + message.payload()));
    }

    @Override
    public void handleBinaryMessage(
        final WebSocketSession session,
        final BinaryMessage message
    ) throws Exception {
        System.out.println(message);
    }

    @Override
    public void handlePongMessage(
        final WebSocketSession session,
        final PongMessage message
    ) throws Exception {
        System.out.println(message);
    }

    @Override
    public void handlePingMessage(
        final WebSocketSession session,
        final PingMessage message
    ) throws Exception {
        System.out.println(message);
    }
}
