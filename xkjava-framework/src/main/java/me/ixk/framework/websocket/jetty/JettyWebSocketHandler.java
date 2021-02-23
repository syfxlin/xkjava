package me.ixk.framework.websocket.jetty;

import java.nio.ByteBuffer;
import me.ixk.framework.exception.WebSocketException;
import me.ixk.framework.websocket.BinaryMessage;
import me.ixk.framework.websocket.CloseStatus;
import me.ixk.framework.websocket.PingMessage;
import me.ixk.framework.websocket.PongMessage;
import me.ixk.framework.websocket.TextMessage;
import me.ixk.framework.websocket.WebSocketHandler;
import me.ixk.framework.websocket.WebSocketSession;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WebSocketPingPongListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 9:29
 */
public class JettyWebSocketHandler
    implements WebSocketListener, WebSocketPingPongListener {

    private static final Logger log = LoggerFactory.getLogger(
        JettyWebSocketHandler.class
    );

    private final WebSocketHandler handler;
    private volatile WebSocketSession session;

    public JettyWebSocketHandler(final WebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onWebSocketConnect(final Session sess) {
        this.session = new JettyWebSocketSession(sess);
        try {
            this.handler.handleConnected(this.session);
        } catch (Exception e) {
            log.error("handleConnected failed", e);
            throw new WebSocketException(e);
        }
    }

    @Override
    public void onWebSocketClose(final int statusCode, final String reason) {
        this.session = null;
        try {
            this.handler.handleClosed(
                    this.session,
                    new CloseStatus(statusCode, reason)
                );
        } catch (Exception e) {
            log.error("handleClosed failed", e);
            throw new WebSocketException(e);
        }
    }

    @Override
    public void onWebSocketError(final Throwable cause) {
        try {
            this.handler.handleError(this.session, cause);
        } catch (Exception e) {
            log.error("handleError failed", e);
            throw new WebSocketException(e);
        }
    }

    @Override
    public void onWebSocketBinary(
        final byte[] payload,
        final int offset,
        final int len
    ) {
        try {
            this.handler.handleMessage(
                    this.session,
                    new BinaryMessage(payload, offset, len)
                );
        } catch (Exception e) {
            log.error("handleMessage failed", e);
            throw new WebSocketException(e);
        }
    }

    @Override
    public void onWebSocketText(final String message) {
        try {
            this.handler.handleTextMessage(
                    this.session,
                    new TextMessage(message)
                );
        } catch (Exception e) {
            log.error("handleMessage failed", e);
            throw new WebSocketException(e);
        }
    }

    @Override
    public void onWebSocketPing(final ByteBuffer payload) {
        try {
            this.handler.handlePingMessage(
                    this.session,
                    new PingMessage(payload)
                );
        } catch (Exception e) {
            log.error("handleMessage failed", e);
            throw new WebSocketException(e);
        }
    }

    @Override
    public void onWebSocketPong(final ByteBuffer payload) {
        try {
            this.handler.handlePongMessage(
                    this.session,
                    new PongMessage(payload)
                );
        } catch (Exception e) {
            log.error("handleMessage failed", e);
            throw new WebSocketException(e);
        }
    }
}
