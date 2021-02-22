package me.ixk.framework.websocket.jetty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.ixk.framework.websocket.BinaryMessage;
import me.ixk.framework.websocket.CloseStatus;
import me.ixk.framework.websocket.PingMessage;
import me.ixk.framework.websocket.PongMessage;
import me.ixk.framework.websocket.TextMessage;
import me.ixk.framework.websocket.WebSocketExtension;
import me.ixk.framework.websocket.WebSocketSession;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:54
 */
public class JettyWebSocketSession implements WebSocketSession {

    private final Session session;
    private final RemoteEndpoint remote;

    public JettyWebSocketSession(Session session) {
        this.session = session;
        this.remote = this.session.getRemote();
    }

    @Override
    public void close(CloseStatus closeStatus) throws IOException {
        this.session.close(
                new org.eclipse.jetty.websocket.api.CloseStatus(
                    closeStatus.getCode(),
                    closeStatus.getReason()
                )
            );
    }

    @Override
    public void sendText(TextMessage message) throws IOException {
        this.remote.sendString(message.payload());
    }

    @Override
    public void sendBinary(BinaryMessage message) throws IOException {
        this.remote.sendBytes(message.payload());
    }

    @Override
    public void sendPing(PingMessage message) throws IOException {
        this.remote.sendPing(message.payload());
    }

    @Override
    public void sendPong(PongMessage message) throws IOException {
        this.remote.sendPong(message.payload());
    }

    @Override
    public boolean isOpen() {
        return this.session.isOpen();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.session.getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.session.getRemoteAddress();
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return this.session.getUpgradeRequest().getHeaders();
    }

    @Override
    public String getProtocolVersion() {
        return this.session.getProtocolVersion();
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        return this.session.getUpgradeRequest()
            .getExtensions()
            .stream()
            .map(e -> new WebSocketExtension(e.getName(), e.getParameters()))
            .collect(Collectors.toList());
    }
}
