package me.ixk.framework.websocket;

import java.nio.charset.StandardCharsets;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:12
 */
public class TextMessage extends WebSocketMessage<String> {

    public TextMessage(final String payload) {
        super(payload);
    }

    public TextMessage(final byte[] payload) {
        super(new String(payload, StandardCharsets.UTF_8));
    }

    @Override
    int length() {
        return this.payload().length();
    }
}
