package me.ixk.framework.websocket;

import java.nio.ByteBuffer;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:09
 */
public class BinaryMessage extends WebSocketMessage<ByteBuffer> {

    public BinaryMessage(final ByteBuffer payload) {
        super(payload);
    }

    public BinaryMessage(final byte[] payload) {
        this(payload, 0, payload.length);
    }

    public BinaryMessage(
        final byte[] payload,
        final int offset,
        final int length
    ) {
        super(ByteBuffer.wrap(payload, offset, length));
    }

    @Override
    int length() {
        return this.payload().remaining();
    }
}
