package me.ixk.framework.websocket;

import java.nio.ByteBuffer;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:11
 */
public class PongMessage extends WebSocketMessage<ByteBuffer> {

    public PongMessage() {
        super(ByteBuffer.allocate(0));
    }

    public PongMessage(final ByteBuffer payload) {
        super(payload);
    }

    @Override
    int length() {
        return this.payload().remaining();
    }
}
