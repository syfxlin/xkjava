package me.ixk.framework.websocket;

import java.nio.ByteBuffer;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:10
 */
public class PingMessage extends WebSocketMessage<ByteBuffer> {

    public PingMessage() {
        super(ByteBuffer.allocate(0));
    }

    public PingMessage(final ByteBuffer payload) {
        super(payload);
    }

    @Override
    int length() {
        return this.payload().remaining();
    }
}
